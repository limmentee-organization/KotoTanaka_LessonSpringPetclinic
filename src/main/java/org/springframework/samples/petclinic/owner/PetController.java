package org.springframework.samples.petclinic.owner;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/owners/{ownerId}")
public class PetController {

	private static final String VIEWS_PETS_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm";
	private final OwnerRepository owners;
	private final PetTypeRepository types;
	
	public PetController(OwnerRepository owners, PetTypeRepository types) {
		this.owners = owners;
		this.types = types;
	}
	
	/*
	 * ◯「ペットタイプのコレクション」をモデルに追加
	 * @ModelAttribute("types")：戻り値をtypesという名前でモデルに追加
	 * ※ populate：データを準備するという意味
	 * ※ Controller内の画面描画メソッド（@GetMapping など）が呼ばれると、画面に遷移する前にこのメソッドが自動実行。
	 * 画面側で ${types} としてデータを使えるようになる
	 * 
	 */
	@ModelAttribute("types")
	public Collection<PetType> populatePetType(){
		return this.types.findPetTypes();
	}
	
	/*
	 * ◯「オーナー」をモデルに追加
	 * ・オプショナルでIdのチェックをし違反する場合は例外をスロー
	 * ・IllegalArgumentException：不正な引数の例外を意味し、非検査例外のためthrowsの記載不要
	 */
	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable ("ownerId") Integer ownerId) {
		Optional<Owner> optionalOwner = this.owners.findById(ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));
		return owner;
	}
	
	/*
	 * ◯「ペット」をモデルに追加
	 * ・PetIdの有無によって更新と新規登録を分けている
	 *　- PetIdがパスに含まれている場合　→更新：PetIdに応じたペットデータを返す
	 *　- PetIdがパスに含まれていない場合　→新規登録：空のPetオブジェクトを返す
	 */
	@ModelAttribute("pet")
	public Pet findPet(@PathVariable("ownerId") int ownerId,
			@PathVariable(name = "petId", required = false) Integer petId) {
		
		if (petId == null) {
			return new Pet();
		}
		
		Optional<Owner> optionalOwner = this.owners.findById(ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));
		return owner.getPet(petId);
	}
	
	/*
	 * ◯ "owner" というモデルのバインディングの事前初期化処理
	 * ・@ModelAttribute("owner") などで渡されたデータを処理する前に、バインディングの初期化処理を実行する命令
	 * ・WebDataBinder dataBinder：HTMLフォームから送信されたパラメーターを、Javaオブジェクトのフィールドに自動注入してくれるSpringの機能
	 * ・dataBinder.setDisallowedFields("id", "*.id")：idや〇〇idなどのフィールドへバインドを禁止する
	 * 　- 悪意のあるユーザーによるデベロッパーツールなど使った不正リクエストを防ぐ
	 * 　- 更新対象の id はURLのパスパラメータ（@PathVariable）などから安全に取得・管理し、リクエストボディからの不用意な上書きを防げる
	 */
	@InitBinder("owner")
	public void initOwnerBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id", "*.id");
	}
	
	/*
	 * ◯ "pet" というモデルのバインディングの事前初期化処理
	 * ・dataBinder.setValidator(new PetValidator())：独自に作成したバリデータ（PetValidator）を適用するための設定
	 */
	@InitBinder("pet")
	public void initPetBinder(WebDataBinder dataBinder) {
		dataBinder.setValidator(new PetValidator());
		dataBinder.setDisallowedFields("id", "*.id");
	}
	
	/*
	 * ◯ペット登録画面初期表示
	 * ・ModelMap model はModel modelとほぼ同じ。Map的なメソッドが使える
	 * ・メソッドないでmodel 使用していないので削除しても動作する
	 */
	@GetMapping("/pets/new")
	public String initCreationForm(Owner owner, ModelMap model) {
		Pet pet = new Pet();
		owner.addPet(pet);
		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}
	
	/*
	 * ◯ペット新規登録処理
	 */
	@PostMapping("pets/new")
	public String processCreationForm(Owner owner, @Valid Pet pet, BindingResult result,
			RedirectAttributes redirectAttributes) {
		
		 // ◯名前の重複チェック
		String petName = pet.getName();
		if(StringUtils.hasText(petName)) {		// 名前が空白じゃなければ進む
			Pet existingPet = owner.getPet(petName, false);		// 同じ名前を検索、falseのため新規登録の場合（id=null）も無視しない
			if (existingPet != null && !Objects.equals(existingPet.getId(), pet.getId())) {		// 同じ名前のペットとIdが一致しない（同じ名前ですでに登録済み）の場合
				result.rejectValue("name", "duplicate", "already exists");
			}
		}
		
		// ◯誕生日の不正チェック
		LocalDate currentDate = LocalDate.now();
		if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(currentDate)) {
			result.rejectValue("birthDate", "typeMismatch.birthDate");
		}
		
		if (result.hasErrors()) {
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}
		
		try {
			owner.addPet(pet);
			this.owners.saveAndFlush(owner);
		} catch(DataIntegrityViolationException ex) {
			if (!isDuplicatePetNameViolation(ex)) {
				throw ex;
			}
			result.rejectValue("name", "duplicate", "already exsist");
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}
		redirectAttributes.addFlashAttribute("message", "New Pet has been Added");
		return "redirect:/owners/{ownerId}";
	}
	
	// ◯更新処理 
//	private void updatePetDetails(Owner owner, Pet pet) {
//		Integer id = pet.getId();
//		Assert.state(id != null, "'pet.getId()' must not be null");
//		
//		Pet existingPet = owner.getPet(id);
//		if()
//	}
	
	/*
	 * ◯DBのユニーク制約違反判定
	 * ・データベースレベルの重複エラーが発生した際に、「それが『同じ飼い主（Owner）による
	 * 　同じペット名（Pet Name）の重複』によって引き起こされたものか」を判定
	 * ・DataIntegrityViolationException ex：Spring Data JPA（Hibernate）がデータベース操作時に
	 * 　制約違反（NOT NULL違反、外部キー制約違反、ユニークキー制約違反など）を検知した際に投げる例外オブジェクト
	 * ・複数ユーザーが同時に同じ登録実行した場合のDBレベルの最終防波堤
	 * ・unique_owner_pet_nameを含むメッセージだった場合にtrueを返しアラートを出す
	 */
	
	private boolean isDuplicatePetNameViolation(DataIntegrityViolationException ex) {
		String message = ex.getMessage();
		return message != null && message.toLowerCase().contains("unique_owner_pet_name");
	}
}

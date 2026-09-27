package org.springframework.samples.petclinic.owner;

import java.util.Collection;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
	
	
	@GetMapping("/pets/new")
	public String initCreationForm(Owner owner, ModelMap model) {
		Pet pet = new Pet();
		owner.addPet(pet);
		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}
}

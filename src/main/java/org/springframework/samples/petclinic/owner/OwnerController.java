package org.springframework.samples.petclinic.owner;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OwnerController {

	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";
	
	/*
	 * ◯コンストラクタ注入（推奨）
	 * @Autowired を用いる注入は「フィールド注入」　※非推奨
	 * ・コンストラクタ注入だとfinalにできる　↔︎フィールド注入では不可
	 * ・単体テストでSpringを起動せず new OwnerController(mockRepository) だダミー（モック）を渡せる
	 * ・OwnerRepository なしで OwnerController を生成できなくなるため、NullPointerExceptionを防げる
	 * OwnerControllerやOwnerRepositoryはSpringコンテナ（ApplicationContext）によってアプリケーション起動時に自動でnew
	 */
	private final OwnerRepository owners;
	
	public OwnerController(OwnerRepository owners) {
		this.owners = owners;
	}
	
	/*
	 * ◯画面からのデータをJavaオブジェクトのフィールドにセット（バインド）する際のルール
	 * WebDataBinder：フォームの文字列（例: "firstName"="Taro"）を、Javaオブジェクトに紐付ける役割を持つSpringのバインディング制御オブジェクト
	 * dataBinder.setDisallowedFields("id", "*.id")：バインドを許可しないフィールド名の指定　※idは自動採番のため
	 * *.id：*はワイルドカードpet.idなども対象外
	 */
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id", "*.id");
	}
	
	/*
	 * ◯OwnerオブジェクトをModelにセットする共通処理
	 * @ModelAttribute("owner")："owner" というキー名で自動的に Model（画面に渡すデータバケツ）にセット
	 * 　※コントローラー内のあらゆる @GetMapping や @PostMapping メソッドが呼ばれる前に毎回自動実行
	 * （@PathVariable(name = "ownerId", required = false) Integer ownerId）：URLからownerIdを引数として受け取る。
	 * required = false：URLにownerIdが含まれなくてもエラーにせずnullとして受け取る　※デフォルトはrequired = true
	 */
	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable(name = "ownerId", required = false) Integer ownerId) {
		return ownerId == null ? new Owner() 
				: this.owners.findById(ownerId)
					.orElseThrow(() -> new IllegalArgumentException("Owner not found with id: " + ownerId 
							+ ". Please ensure the ID is correct " + "and the owner exsits in the database."));
	}
	
	/*
	 * ◯オーナー検索ページ
	 * @GetMapping：ユーザーからのGETリクエストを捕捉する
	 * (...)内はアドレスバーのURLと一致させる
	 * "http://localhost:8080/owners/find"の場合は、"/owners/find"を指定
	 * 先頭の/がなくても動作するが記述するのがマナー
	 */
	@GetMapping("/owners/find")
	public String initFindForm() {
		return "owners/findOwners";
	}
	
	/*
	 * ◯オーナー一覧の取得
	 */
	@GetMapping("/owners")
	public String processFindForm(@RequestParam(defaultValue = "1") int page, Owner owner, 
			BindingResult result, Model model) {
		// lastNameの取得
		String lastName = owner.getLastName();
		if (lastName == null) {
			lastName = "";		// 条件なしで検索　※/ownersパラメータなしのGETリクエストに対しては全てのレコードを返す
		}
		
		// オーナー一覧の検索
		Page<Owner> ownersResults = findPaginatedForOwnersLastName(page, lastName);
		// オーナーが見つからない場合
		if (ownersResults.isEmpty()) {
			result.rejectValue("lastName", "notFound", "not found");		// バリデーションエラーを追加　※引数：フィールド名、エラーコード、デフォルトメッセージ 　※message.propertiesで上書き
			return "owners/findOwners";		//オーナー検索画面のHTMLテンプレートを返す
		}
		
		// オーナーが1件ヒットした場合
		if (ownersResults.getTotalElements() == 1) {
			owner = ownersResults.iterator().next();
			return "redirect:/owners/" + owner.getId();			// オーナー詳細画面にリダイレクトするURLを返す
		}
		
		// 複数のオーナーが見つかった場合
		return addPaginationModel(page, model, ownersResults);
	}
	
	/*
	 * ◯モデルにオーナーデータやページネーション用のデータを格納しビューに渡す処理
	 */
	private String addPaginationModel(int page, Model model, Page<Owner> paginated) {
		List<Owner> listOwners = paginated.getContent();		// - getContent()： 1ページに表示するための、実際のOwnerデータ
		model.addAttribute("currentPage", page);		// @RequestParamで受け撮った現在のページ
		model.addAttribute("totalPages", paginated.getTotalPages());		// 全部で何ページ分あるかの数字
		model.addAttribute("totalItems", paginated.getTotalElements());		//データベースに全部で何人登録されているかの総数
		model.addAttribute("listOwners", listOwners);
		return "owners/ownersList";
	}
	
	/*
	 * ◯ページネーションの設定
	 * Pageable（インターフェース）：ページングに必要な情報（ページ番号、1ページの件数、ソート順など）を持つオブジェクトの規格（ルール）
	 * PageRequest.of(page - 1, pageSize);：Pageable インターフェースを実装した Spring Data の具象クラス。DBから何件目のデータを、何件取得するかを指定。
	 * 		画面・ユーザー視点: 最初のページは 1 
	 * 		Spring Data 内部視点: 最初のページは 0
	 * 		＜SQL＞に変換すると...
	 * 			SELECT * FROM owners 
	 * 			WHERE last_name LIKE '検索文字%' 
	 * 			LIMIT 5 OFFSET 5;
	 * 			※ LIMIT 5: pageSize の値（5件取得）
	 * 			※ OFFSET 5: (page - 1) * pageSize の計算結果（最初の5件を読み飛ばして6件目から取得）
	 */
	private Page<Owner> findPaginatedForOwnersLastName(int page, String lastname){
		int pageSize = 5;
		Pageable pageable = PageRequest.of(page - 1, pageSize);
		return owners.findByLastNameStartingWith(lastname, pageable);
	}
	
	/*
	 * ◯オーナー情報更新画面表示
	 */
	@GetMapping("/owners/{ownerId}/edit")
	public String initUpdateOwnerForm() {
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}
	
	/*
	 * ◯オーナー情報更新処理
	 * @Valid：バリデーションチェックのスイッチ　※@Validatedはグループ分けも可能になるが通常は@ValidでOK
	 * RedirectAttributes と addFlashAttribute：
	 * バリデーションエラーの場合はhtmlテンプレートを返し、PRGにしない。
	 * →入力内容とエラーをそのまま画面に戻すため（ユーザー入力値は保存される）
	 * →PRGパターンにすると別のGETリクエストとなり引き継がれない
	 * Objects.equals(owner.getId(), ownerId)：null安全で比較可能、Integer型とint型の比較
	 * result.rejectValue：BindingResultに手動でエラーを追加する。項目（フィールド）, エラーコード, エラーメッセージの順で引数に渡す
	 * 
	 */
	@PostMapping("/owners/{ownerId}/edit")
	public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result, 
			@PathVariable("ownerId") int ownerId, RedirectAttributes redirectAttribute) {
		
		// バリデーションチェック
		if (result.hasErrors()) {
			redirectAttribute.addFlashAttribute("error", "There was an error in updateing the owner.");		// PRGパターンではないのでmodel.addAttributeでも良さそう
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}
		
		// idの不正リクエストチェック
		if (!Objects.equals(owner.getId(), ownerId)) {
			result.rejectValue("id", "mismatch", "The owner ID in the form dose not match the URL.");
			redirectAttribute.addFlashAttribute("error", "Owner ID mismatch. Please try again.");
			return "redirect:/owners/{ownerId}/edit";
		}
		
		// オーナー情報更新処理
		owner.setId(ownerId);
		this.owners.save(owner);
		redirectAttribute.addFlashAttribute("message", "Owner Values Updated");
		return "redirect:/owners/{ownerId}";
		
	}
	
	/*
	 * ◯オーナー詳細画面表示
	 * ModelAndView：ViewとModelをまとめて返す。以下のコードと同じ。
	 * public String showOwner(@PathVariable("ownerId") int ownerId, Model model){
	 * 		Optional<Owner> optionalOwner  = this.owner.findById(ownerId);
	 * 		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct"));
	 * 		model.addAttribute("owner", owner);
	 * 		return "owners/ownerDetails";
	 * }
	 */
	@GetMapping("/owners/{ownerId}")
	public ModelAndView showOwner(@PathVariable("ownerId") int ownerId) {
		ModelAndView mav = new ModelAndView("owners/ownerDetails");		//view：owners/ownerDetails.htmlを指定
		Optional<Owner> optionalOwner = this.owners.findById(ownerId);
		Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
				"Owner not found with id: " + ownerId + ". Please ensure the ID is correct"));
		mav.addObject(owner);		// mav.addObject("owner", owner);と同じ。ModelAndViewを使用する時はaddObjectを使用
		return mav;		// ModelAndViewオブジェクトを返すようにする
	}
}

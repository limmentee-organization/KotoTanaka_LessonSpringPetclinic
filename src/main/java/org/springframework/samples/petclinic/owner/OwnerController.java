package org.springframework.samples.petclinic.owner;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OwnerController {

//	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";
	
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
			result.rejectValue("lastName", "notFound", "not found");		// バリデーションエラーを追加　※引数：フィールド名、エラーコード、デフォルトメッセージ 
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
		model.addAttribute("totalPage", paginated.getTotalPages());		// 全部で何ページ分あるかの数字
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
	
	
	
}

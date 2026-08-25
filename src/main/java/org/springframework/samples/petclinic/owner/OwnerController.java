package org.springframework.samples.petclinic.owner;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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
	
	/*◯FindOwnerページ
	 * @GetMapping：ユーザーからのGETリクエストを捕捉する
	 * (...)内はアドレスバーのURLと一致させる
	 * "http://localhost:8080/owners/find"の場合は、"/owners/find"を指定
	 *先頭の/がなくても動作するが記述するのがマナー
	 */
	@GetMapping("/owners/find")
	public String initFindForm() {
		return "owners/findOwners";
	}
}

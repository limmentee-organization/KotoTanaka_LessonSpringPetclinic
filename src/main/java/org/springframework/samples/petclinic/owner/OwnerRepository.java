package org.springframework.samples.petclinic.owner;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Integer> {

	/*
	 * ◯苗字からオーナーリストを取得するメソッド
	 * Spring Data JPAのルールで動作
	 * findBy〜：〜を探す。findByLastNameの場合、 "LastName" に一致するリストを探してくる
	 * StartingWith：前方一致 "LIKE '検索文字%'" で取得。引数が"田"なら、田中も田口もOK
	 * (..., Pageable pageable)：1ページの表示件数や並び順のルール（ID順）などのページ分けのルールを引数に渡す
	 * Page<Owner>：検索結果を1ページ分の塊で返す。次ページはあるか？などの情報も一緒に詰め込んだ箱
	 * 		- getContent()： 1ページ目に表示するための、実際のOwnerデータ（例：最初の10人分など）。
	 * 		- getTotalPages() ： 全部で何ページ分あるかの数字
	 * 		- getTotalElements() ：データベースに全部で何人登録されているかの総数
	 * 		- hasNext() ：次のページはあるか？（あれば true）
	 * 		- hasPrevious() ：前のページはあるか？
	 * ＜参考＞https://qiita.com/ki_takada/items/e4b56f835ca65b406b45
	 */
	Page<Owner> findByLastNameStartingWith(String lastName, Pageable pageable);
	
	Optional<Owner> findById(Integer id);
}

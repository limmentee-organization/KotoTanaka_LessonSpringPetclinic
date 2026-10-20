package org.springframework.samples.petclinic.vet;
import java.util.Collection;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

/*
 * ◯獣医師のリポジトリ
 * ・Repository<Vet, Integer>：Spring Data JPAの機能を利用するための基底インターフェース
 * 　第一引数：捜査対象のエンティティ、第二引数：捜査対象のID（PK）の型
 * ・JpaRepositoryインターフェースを継承すれば標準のfindAllを実装なしで使用可能 ↔︎ Repositoryは自分で実装が必要（明示してアノテーション付加する場合など）
 * ・JpaRepositoryで使える標準finsAll系メソッド
 *  　- List<T> findAll()：全件取得
 *  　-Page<T> findAll(Pageable pageable)：ページ分けとソート適用
 *  　-List<T> findAll(Sort sort)：ソート適用し全件取得
 *  　-List<T> findAllById(Iterable<ID> ids)：指定した複数IDに一致するデータを取得
 *  
 */
public interface VetRepository extends Repository<Vet, Integer> {

	/*
	 * ◯全獣医師データ取得
	 * ・@Transactional(readOnly = true)：データの「読み取り専用」、書き換えは不可のためDB最適化
	 * ・@Cacheable("vets")：データをキャッシュに保存。2回目以降はDBアクセスせずキャッシュから高速にデータを返す　※vetsはキャッシュ保管場所の名前
	 * ・Collection<Vet>型に格納するのは汎用性・抽象度を高め柔軟にデータを扱うため
	 * ・throws DataAccessException：SpringFrameworkが提供する「データベース操作に関するエラー（例外）をまとめたクラス」
	 * 　通常はMySQLやPostgreSQLなどDBごとに異なるエラー→Spring共通の非検査例外（RutimeException）に変換して扱える
	 */
	@Transactional(readOnly = true)
	@Cacheable("vets")
	Collection<Vet> findAll() throws DataAccessException;
	
	/*
	 * ◯ページネーション適用して獣医師データ取得
	 * ・Page<Vet>やPageable：SpringDataの仕組み、ページネーションやソートを簡単に扱える
	 * ・Pageable：何ページ目を何件取得し、どの項目でソートするかなどの検索条件をまとめたオブジェクト
	 * ・Page<Vet>：取得したデータ（List<Vet>）＋付属情報（全件数、総ページ数、現在のページ番号、次ページ有無の判定フラグ）
	 */
	@Transactional(readOnly = true)
	@Cacheable("vets")
	Page<Vet> findAll(Pageable pageable) throws DataAccessException;
}

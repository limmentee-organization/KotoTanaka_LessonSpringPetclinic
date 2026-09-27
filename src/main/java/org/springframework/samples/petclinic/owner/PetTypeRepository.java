package org.springframework.samples.petclinic.owner;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/*
 * ◯PetTypeRepositoryの定義
 * ジェネリクスには「どのエンティティクラスを扱うか」「主キー（ID）の型は何か」を渡す
 */
public interface PetTypeRepository extends JpaRepository<PetType, Integer> {
	
	/*
	 * ◯メソッド定義
	 * @Query：独自の検索クエリを直接記述可能となるアノテーション
	 * 　- SQLではなくJPQL（Java Persistence Query Language）で書く
	 * 　- DBのテーブル・カラムではなくJavaのエンティティ・フィールドに対しクエリ発行
	 * FROM PetType ptype：PetType クラスを参照しptypeとして扱う
	 * SELECT ptype：PetTypeオブジェクト全てを抽出
	 * ORDER BY ptype.name：ptypeのname フィールドを使って並び替え
	 */
	@Query("SELECT ptype FROM PetType ptype ORDER BY ptype.name")
	List<PetType> findPetTypes();
	
}

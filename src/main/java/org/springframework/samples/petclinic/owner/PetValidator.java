package org.springframework.samples.petclinic.owner;

import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class PetValidator implements Validator {
	
	public static final String REQUIRED = "required";
	
	/*
	 * ◯自作のバリデーション記述　※実務ではほぼ@Validで完結
	 * ・Object obj：検証対象のオブジェクト（Springから汎用的な Object 型で渡される）
	 * ・Errors errors：入力エラーが発生した場合に、エラー情報（フィールド名やエラーコード）を記録するためのSpringのオブジェクト
	 * ・Pet pet = (Pet) obj;：Pet クラスのフィールド（getName() など）にアクセスできるように、Pet 型へキャスト（変換）
	 */
	@Override
	public void validate(Object obj, Errors errors) {
		Pet pet = (Pet) obj;
		
		/*
		 * ◯名前のバリデーション
		 * ・StringUtils.hasText(name)：テキストを持っているか？※名前が null でなく、空文字（""）でもなく、スペース（空白文字）だけでもない
		 * ・errors.rejectValue："name" フィールドにエラーがあることを記録
		 */
		String name = pet.getName();
		if (!StringUtils.hasText(name)) {
			errors.rejectValue("name", REQUIRED, REQUIRED);
		}
		
		/*
		 * ◯ペット種別のバリデーション
		 * ・新規登録時ペットの種類が選ばれていなければエラーにする
		 */
		if (pet.isNew() && pet.getType() == null) {
			errors.rejectValue("type", REQUIRED, REQUIRED);
		}
		
		/*
		 * ◯誕生日のバリデーション
		 * ・誕生日のにゅりょくがなければエラーにする
		 */
		if (pet.getBirthDate() == null) {
			errors.rejectValue("birthDate", REQUIRED, REQUIRED);
		}
	}

	/*
	 * ◯Pet専用バリデータであることの宣言
	 * ・このバリデータは Pet クラス（またはそのサブクラス）のデータチェック専用であることをSpringに宣言
	 * ・Class<?> clazz: 引数で渡された clazz が、Pet クラスまたは Petのサブクラスであるかを判定
	 * ・このバリデータでチェック可能な型であれば true、対応していない型であれば false を返す
	 * ・Pet.class.isAssignableFrom(clazz)：Pet.class.equals(clazz)ではなくisAssignableFrom を使うのは、継承関係に対応するため
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return Pet.class.isAssignableFrom(clazz);
	}
}

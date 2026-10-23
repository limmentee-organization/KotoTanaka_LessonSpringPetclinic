package org.springframework.samples.petclinic.system;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

/*
 * ◯多言語対応（国際化 / i18n: Internationalization）機能を提供する設定クラス
 * ・@Configuration：SpringのWeb 関連の設定クラスであることを宣言
 * ・@SuppressWarnings("unused")：使われていないコードがある（unused）という種類の警告を消す
 * 　　- "unused"	定義されているのに呼び出されていない変数やメソッドの警告
 * 　　- "unchecked"	型の安全性が確認できない（キャスト関連の）警告
 * 　　- "deprecation"	非推奨（古くなって使わなくなった）メソッドを使っている警告
 * 　　- "all"	すべての警告
 * ・WebMvcConfigurer：Spring MVC の標準動作（割り込み処理 = インターセプターの追加など）をカスタマイズするためのインターフェース
 * ・@Beanと@Componentの違い
 * 　　- @Bean：@Configuration 内のメソッド の上に付け、外部ライブラリのクラスを使うときや、複雑な初期化設定をして登録したいときに使用
 * 　　- @Component：クラスの上（@Controllerなどと同様）に付け、自作クラスを自動検出（コンポーネントスキャン）して管理させたいときに使用
 */
@Configuration
@SuppressWarnings("unused")
public class WebConfiguration implements WebMvcConfigurer {

	/*
	 * ◯言語設定の保持とデフォルト言語の設定
	 * ・LocaleResolver：ユーザーの言語（ロケール）をどうやって取得し、どこに保存するか」というルールだけを定義したインターフェース
	 * ・SessionLocaleResolver：ユーザーの言語選択情報を HTTP セッションに保持（ブラウザを閉じるまでの間）する具象クラス
	 * 　→一度言語を切り替えると画面を遷移しても選択した言語が維持される
	 * ・setDefaultLocale(Locale.ENGLISH)：デフォルトを英語に設定
	 */
	@Bean
	public LocaleResolver localResolver() {
		SessionLocaleResolver resolver = new SessionLocaleResolver();
		resolver.setDefaultLocale(Locale.ENGLISH);
		return resolver;
	}
	
	/*
	 * ◯URLからの言語変更パラメータ検知
	 * ・リクエストの URL に含まれる特定のパラメータを監視し、言語変更のリクエストがあったかを判定
	 * ・setParamName("lang")：監視するパラメータ名を "lang" に指定しています。
	 * ・LocaleChangeInterceptor：ユーザーの言語切り替えを自動検知してロケール（言語設定）を変更する割り込み処理クラス
	 * 　※リクエストのURLに含まれるパラメータ（例: ?lang=ja）を監視する
	 * 　※interceptor：Controllerの処理が実行される直前に割り込み
	 */
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
		interceptor.setParamName("lang");
		return interceptor;
	}
	
	/*
	 * ◯インターセプターの有効化
	 * ・localeChangeInterceptor を Spring MVC の実行パイプラインに登録
	 * 　→ユーザーがどの URL にアクセスしても、リクエストの直前に言語判定（?lang=XX のチェック）が自動実行される
	 * 　例）http://localhost:8080/owners?lang=es にアクセスすると、画面上のメッセージがスペイン語（messages_es.properties で定義した文言）に切り替わる
	 * ・InterceptorRegistry：Spring MVCに対して 「作成したインターセプター（割り込み処理）をどのURLに適用するか」を登録・管理するクラス
	 * 　※.addInterceptor(...) と書くと、全リクエスト（全てのURL）に対して動く
	 * 　※特定のパスだけに絞り込んだり除外したりする細かい設定も可能
	 * 　例）registry.addInterceptor(...).addPathPatterns("/admin/**").excludePathPatterns("/login")
	 * ・localeChangeInterceptor()：LocaleChangeInterceptorがBean化されSpringに保管されているので毎回newせずに登録済みのものを自動呼び出し
	 * 　※ Bean化せずに書くなら以下のようになる
	 * 　@Override
			public void addInterceptors(InterceptorRegistry registry) {
				// @Bean メソッドを用意せず、その場で new して設定・渡す
				LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
				interceptor.setParamName("lang");
				
				registry.addInterceptor(interceptor);
			}
	 */
	public void addInterceptor(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}
	
}

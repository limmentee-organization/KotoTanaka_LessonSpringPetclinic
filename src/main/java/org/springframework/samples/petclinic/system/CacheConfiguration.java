package org.springframework.samples.petclinic.system;

import javax.cache.configuration.MutableConfiguration;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * 
 * ◯キャッシュ機能の設定クラス
 * ・JCache（JSR-107）標準 API を使ったキャッシュ機能（画面表示やデータの高速化）を初期化・設定する
 * ・@Configuration：このクラスが Spring の設定クラス（Bean を定義するクラス）であることを示す
 * ・(proxyBeanMethods = false)：プロキシ生成をスキップして起動時間を短縮・軽量化する設定？？
 * ・@EnableCaching：アプリケーション全体で Spring のキャッシュ機能（@Cacheable など）を有効化
 */
@Configuration(proxyBeanMethods = false)
@EnableCaching
class CacheConfiguration {

	/*
	 * ◯Bean定義？？
	 * ・JCacheManagerCustomizer：Spring Boot が JCache のキャッシュマネージャーを起動する際、独自の設定を割り込ませるためのインターフェース？？
	 * ・cm.createCache("vets", cacheConfiguration())
	 * 　- "vets" という名前のキャッシュ領域（バケツ）を新しく作成
	 * 　- 獣医師（Vets）のデータ取得処理（@Cacheable("vets") がついたメソッド）が実行された際、結果はこの "vets" キャッシュ領域に保持
	 * ◯メリット
	 * ・データベースへの負荷軽減と処理の高速化
	 *  　- キャッシュなしの場合、ユーザーが「獣医師一覧（Vets）」ページを開くたびに、毎回データベースへSQLを実行して重い取得処理を行う
	 *  　- ありの場合、初回アクセス時にDBから取得した結果を "vets" という名前のキャッシュに一時保存。2回目以降のアクセスでは、キャッシュから一瞬でデータを返す
	 */
	@Bean
	public JCacheManagerCustomizer petclinicCacheConfigurationCustomizer() {
		return cm -> cm.createCache("vets", cacheConfiguration());
	}
	/*
	 * ◯キャッシュの詳細設定
	 * ・new MutableConfiguration<>()：JCache（JSR-107）の標準仕様、書き換え可能なキャッシュ設定オブジェクト
	 * ・.setStatisticsEnabled(true)：キャッシュの統計情報の収集を有効化。JMX（Java Management Extensions）経由で外部ツールからキャッシュの利用状況を監視可能
	 */
	private javax.cache.configuration.Configuration<Object, Object> cacheConfiguration(){
		return new MutableConfiguration<>().setStatisticsEnabled(true);
	}
}

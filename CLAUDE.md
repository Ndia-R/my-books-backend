# My Books Backend - 開発ガイド

このファイルは、書籍管理システム「My Books Backend」について、将来の Claude インスタンスが効果的に作業できるよう包括的な情報を提供します。

## プロジェクト概要

**My Books Backend** は Spring Boot 3.3.5 と Java 17 で構築された書籍管理 REST API です。ユーザー認証、書籍管理、レビュー、お気に入り、ブックマーク機能を提供します。

### 主要技術スタック
- **フレームワーク**: Spring Boot 3.3.5
- **Java**: 17
- **データベース**: MySQL (JPA/Hibernate)
- **認証**: JWT トークンベース認証
- **ドキュメント**: OpenAPI 3 (Swagger)
- **マッピング**: MapStruct 1.5.5
- **ビルドツール**: Gradle

## ビルド・開発コマンド

### 基本コマンド
```bash
# プロジェクトのビルド
./gradlew build

# テスト実行
./gradlew test

# アプリケーション起動
./gradlew bootRun

# JAR ファイル生成（my-books.jar として生成される）
./gradlew bootJar

# 依存関係確認
./gradlew dependencies

# クリーンビルド
./gradlew clean build
```

### 重要な設定
- **出力JAR名**: `my-books.jar` (build.gradle で設定)
- **実行ポート**: デフォルト（8080、application.properties で変更可能）

## アーキテクチャとディレクトリ構造

### レイヤーアーキテクチャ
```
Controller → Service → Repository → Entity
     ↓         ↓
   DTO ← Mapper
```

### パッケージ構造
```
com.example.my_books_backend/
├── config/          # 設定クラス（Security, Swagger, Async等）
├── controller/      # REST API エンドポイント
├── dto/            # データ転送オブジェクト
├── entity/         # JPA エンティティ
├── exception/      # カスタム例外とエラーハンドリング
├── mapper/         # MapStruct マッパーインターフェース
├── repository/     # JPA リポジトリ
├── service/        # ビジネスロジック
└── util/          # ユーティリティクラス
```

## 重要な設計パターン

### 1. エンティティ設計
- **基底クラス**: `EntityBase` - すべてのエンティティが継承
  - `createdAt`, `updatedAt`, `isDeleted` フィールドを提供
  - 自動タイムスタンプ更新（`@PrePersist`, `@PreUpdate`）
  - 論理削除対応

### 2. ページネーション戦略
- **ユーティリティ**: `PageableUtils`
- **デフォルト設定**:
  - ページサイズ: 20
  - 最大ページサイズ: 100
  - ソート: `id.asc`
- **2クエリ戦略**: 大量データの効率的な取得
- **レスポンス**: `PageResponse<T>` で統一

### 3. セキュリティ設計
- **JWT認証**: Access Token + Refresh Token
- **パスワード暗号化**: BCrypt
- **CORS**: localhost パターンで設定
- **エンドポイント分類**:
  - 完全パブリック: `/login`, `/signup`, `/logout` など
  - GET のみパブリック: `/books`, `/genres` など

### 4. 例外処理
- **カスタム例外**: 
  - `NotFoundException`, `BadRequestException`, `ConflictException`
  - `UnauthorizedException`, `ForbiddenException`, `ValidationException`
- **統一エラーレスポンス**: `ErrorResponse` クラス
- **グローバルハンドラ**: `ExceptionControllerAdvice`

## データベース設計

### 主要エンティティ
1. **User** - ユーザー情報（Spring Security UserDetails 実装）
2. **Book** - 書籍情報（文字列ID、レビュー統計含む）
3. **Genre** - ジャンル（多対多関係）
4. **Review** - レビュー（評価、コメント）
5. **Favorite** - お気に入り
6. **Bookmark** - ブックマーク
7. **Role** - ロール（ADMIN, USER）

### データベース設定
```properties
# 設定場所: src/main/resources/application.properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.jpa.properties.hibernate.hbm2dll.create_namespaces=true
```

### 環境変数
```bash
SPRING_DATASOURCE_URL      # データベース接続URL
SPRING_DATASOURCE_USERNAME # データベースユーザー名
SPRING_DATASOURCE_PASSWORD # データベースパスワード
SPRING_APP_JWT_SECRET      # JWT署名シークレット
SPRING_APP_JWT_ACCESS_EXPIRATION  # アクセストークン有効期限
SPRING_APP_JWT_REFRESH_EXPIRATION # リフレッシュトークン有効期限
```

## API 設計

### 認証エンドポイント
- `POST /login` - ログイン
- `POST /signup` - ユーザー登録
- `POST /logout` - ログアウト
- `POST /refresh-token` - トークンリフレッシュ

### 書籍関連エンドポイント
- `GET /books` - 書籍一覧（ページネーション、ソート）
- `GET /books/new-releases` - 新着書籍
- `GET /books/search` - 書籍検索
- `GET /books/discover` - ジャンル別書籍発見
- `GET /books/{id}` - 書籍詳細
- `GET /books/{id}/toc` - 目次
- `GET /books/{id}/reviews` - レビュー一覧

### デフォルトパラメータ
```java
// 書籍関連
DEFAULT_BOOKS_START_PAGE = "1"
DEFAULT_BOOKS_PAGE_SIZE = "20"
DEFAULT_BOOKS_SORT = "popularity.desc"

// レビュー関連
DEFAULT_REVIEWS_START_PAGE = "1"
DEFAULT_REVIEWS_PAGE_SIZE = "3"
DEFAULT_REVIEWS_SORT = "updatedAt.desc"
```

## テスト構造

### テスト設定
- **フレームワーク**: JUnit 5
- **基本テスト**: `MyBooksBackendApplicationTests` - コンテキスト読み込みテスト
- **テスト実行**: `./gradlew test`

### テスト戦略（推奨）
1. **単体テスト**: Service層のビジネスロジック
2. **統合テスト**: Repository層のデータアクセス
3. **APIテスト**: Controller層のエンドポイント

## 開発規約

### 1. ネーミング規約
- **エンティティ**: PascalCase（例: `User`, `BookChapter`）
- **フィールド**: camelCase（例: `createdAt`, `averageRating`）
- **テーブル**: snake_case（例: `users`, `book_chapters`）
- **API エンドポイント**: kebab-case（例: `/new-releases`）

### 2. パッケージ構成規約
- **Controller**: REST API の責務のみ
- **Service**: ビジネスロジックの実装
- **Repository**: データアクセスの抽象化
- **DTO**: API入出力の専用オブジェクト
- **Mapper**: Entity ↔ DTO 変換

### 3. セキュリティ規約
- **認証が必要なエンドポイント**: デフォルト
- **パブリックエンドポイント**: `SecurityEndpointsConfig` で明示的に設定
- **パスワード**: 必ず BCrypt で暗号化
- **JWT**: HttpOnly Cookie でリフレッシュトークン管理

## 重要な設定ファイル

### 1. `application.properties`
- データベース接続設定
- JWT設定
- ページネーション設定
- JPA/Hibernate設定

### 2. `SecurityConfig.java`
- Spring Security設定
- CORS設定
- JWT フィルター設定
- エンドポイントアクセス制御

### 3. `SwaggerConfig.java`
- OpenAPI設定
- JWT認証スキーム設定

### 4. `AsyncConfig.java`
- 非同期処理設定
- 書籍統計更新用スレッドプール

## 開発時の注意点

### 1. MapStruct + Lombok の依存関係
```gradle
// annotation processor の順序が重要
annotationProcessor 'org.projectlombok:lombok'
annotationProcessor 'org.mapstruct:mapstruct-processor:1.5.5.Final'
annotationProcessor 'org.projectlombok:lombok-mapstruct-binding:0.2.0'
```

### 2. JPA パフォーマンス対策
- **N+1問題対策**: `@EntityGraph`, `JOIN FETCH`
- **2クエリ戦略**: 大量データでのソート順序維持
- **lazy loading**: `spring.jpa.open-in-view=false`

### 3. ページネーション実装
- **1ベース**: API は1ベースページング
- **0ベース**: JPA Pageable は内部的に0ベース
- **変換**: `PageableUtils.toPageResponse()` で統一

### 4. 論理削除パターン
- **フィールド**: `isDeleted` boolean フィールド
- **クエリ**: `findByIsDeletedFalse()` パターン
- **デフォルト値**: `false`

## トラブルシューティング

### 1. ビルドエラー
```bash
# Gradle Wrapper の権限エラー
chmod +x gradlew

# 依存関係の競合
./gradlew clean build

# MapStruct 生成コードエラー
./gradlew clean compileJava
```

### 2. 認証エラー
- JWT シークレットの環境変数設定確認
- トークン有効期限の確認
- CORS設定の確認

### 3. データベース接続エラー
- 環境変数の設定確認
- MySQL サーバーの起動確認
- データベース権限の確認

## ドキュメント

### Swagger UI
- URL: `http://localhost:8080/swagger-ui.html`
- JWT認証: Bearer Token形式
- 全エンドポイントの詳細仕様を確認可能

### API仕様
- OpenAPI 3 形式で自動生成
- DTOクラスからスキーマ自動生成
- 認証スキーム設定済み

## 今後の開発指針

### 1. 新機能追加時
1. DTO クラス作成（リクエスト/レスポンス）
2. エンティティ拡張（必要に応じて）
3. Repository メソッド追加
4. Service 層でビジネスロジック実装
5. Controller 層でAPI公開
6. Mapper でEntity/DTO変換
7. テスト作成

### 2. セキュリティ考慮事項
- 新エンドポイントのアクセス制御設定
- 入力値バリデーション
- SQL インジェクション対策（JPA使用により基本対策済み）
- XSS対策（JSON API のため基本対策済み）

### 3. パフォーマンス考慮事項
- データベースインデックス設計
- クエリ最適化（N+1問題対策）
- キャッシュ戦略（Spring Cache使用準備済み）
- 非同期処理活用（統計更新等）

このドキュメントを参考に、一貫性のある高品質なコードの開発を進めてください。
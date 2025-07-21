# My Books Backend - 開発ガイド

このファイルは、書籍管理システム「My Books Backend」について、将来の Claude インスタンスが効果的に作業できるよう包括的な情報を提供します。

## プロジェクト概要

**My Books Backend** は Spring Boot 3.3.5 と Java 17 で構築された書籍管理 REST API です。ユーザー認証、書籍管理、レビュー、お気に入り、ブックマーク、章ページ機能を提供する包括的な書籍システムです。

### 主要技術スタック
- **フレームワーク**: Spring Boot 3.3.5
- **Java**: 17
- **データベース**: MySQL 8.0 (JPA/Hibernate)
- **認証**: JWT トークンベース認証（Access Token + Refresh Token）
- **ドキュメント**: OpenAPI 3 (Swagger UI)
- **マッピング**: MapStruct 1.5.5
- **セキュリティ**: Spring Security 6
- **依存性注入**: Lombok
- **JWT**: Auth0 Java JWT 4.4.0
- **ビルドツール**: Gradle
- **開発環境**: Docker & Docker Compose

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

### Docker 開発環境
```bash
# 開発環境の起動
docker-compose up -d

# アプリケーションのみ再起動
docker-compose restart app

# ログ確認
docker-compose logs -f app

# 開発環境の停止
docker-compose down
```

### 重要な設定
- **出力JAR名**: `my-books.jar` (build.gradle で設定)
- **実行ポート**: 8080（Docker環境）
- **データベースポート**: 3306（Docker環境）

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
├── config/          # 設定クラス
│   ├── AsyncConfig.java           # 非同期処理設定
│   ├── AuthTokenFilter.java       # JWT認証フィルター
│   ├── SecurityConfig.java        # Spring Security設定
│   ├── SecurityEndpointsConfig.java # エンドポイントアクセス制御
│   └── SwaggerConfig.java         # Swagger/OpenAPI設定
├── controller/      # REST API エンドポイント
│   ├── AdminUserController.java   # 管理者用ユーザー管理
│   ├── AuthController.java        # 認証（ログイン/サインアップ）
│   ├── BookController.java        # 書籍関連
│   ├── BookmarkController.java    # ブックマーク
│   ├── FavoriteController.java    # お気に入り
│   ├── GenreController.java       # ジャンル
│   ├── ReviewController.java      # レビュー
│   ├── RoleController.java        # ロール
│   └── UserController.java        # ユーザープロフィール
├── dto/            # データ転送オブジェクト
│   ├── PageResponse.java          # ページネーションレスポンス
│   ├── auth/                      # 認証関連DTO
│   ├── book/                      # 書籍関連DTO
│   ├── book_chapter/              # 書籍章関連DTO
│   ├── book_chapter_page_content/ # 書籍ページコンテンツDTO
│   ├── bookmark/                  # ブックマークDTO
│   ├── favorite/                  # お気に入りDTO
│   ├── genre/                     # ジャンルDTO
│   ├── review/                    # レビューDTO
│   ├── role/                      # ロールDTO
│   └── user/                      # ユーザーDTO
├── entity/         # JPA エンティティ
│   ├── base/
│   │   └── EntityBase.java        # 基底エンティティ
│   ├── enums/
│   │   └── RoleName.java          # ロール名enum
│   ├── Book.java                  # 書籍
│   ├── BookChapter.java           # 書籍章
│   ├── BookChapterId.java         # 書籍章複合主キー
│   ├── BookChapterPageContent.java # 書籍ページコンテンツ
│   ├── BookChapterPageContentId.java # ページコンテンツ複合主キー
│   ├── Bookmark.java              # ブックマーク
│   ├── Favorite.java              # お気に入り
│   ├── Genre.java                 # ジャンル
│   ├── Review.java                # レビュー
│   ├── Role.java                  # ロール
│   └── User.java                  # ユーザー
├── exception/      # カスタム例外とエラーハンドリング
│   ├── BadRequestException.java
│   ├── ConflictException.java
│   ├── ErrorResponse.java         # 統一エラーレスポンス
│   ├── ExceptionControllerAdvice.java # グローバル例外ハンドラ
│   ├── ForbiddenException.java
│   ├── NotFoundException.java
│   ├── UnauthorizedException.java
│   └── ValidationException.java
├── mapper/         # MapStruct マッパーインターフェース
│   ├── BookMapper.java
│   ├── BookmarkMapper.java
│   ├── FavoriteMapper.java
│   ├── GenreMapper.java
│   ├── ReviewMapper.java
│   ├── RoleMapper.java
│   └── UserMapper.java
├── repository/     # JPA リポジトリ
│   ├── BookChapterPageContentRepository.java
│   ├── BookChapterRepository.java
│   ├── BookRepository.java
│   ├── BookmarkRepository.java
│   ├── FavoriteRepository.java
│   ├── GenreRepository.java
│   ├── ReviewRepository.java
│   ├── RoleRepository.java
│   └── UserRepository.java
├── service/        # ビジネスロジック（インターフェース）
│   ├── impl/       # サービス実装
│   │   ├── BookServiceImpl.java
│   │   ├── BookStatsServiceImpl.java
│   │   ├── BookmarkServiceImpl.java
│   │   ├── FavoriteServiceImpl.java
│   │   ├── GenreServiceImpl.java
│   │   ├── ReviewServiceImpl.java
│   │   ├── RoleServiceImpl.java
│   │   ├── UserDetailsServiceImpl.java
│   │   └── UserServiceImpl.java
│   ├── AuthService.java           # 認証サービス
│   ├── BookService.java
│   ├── BookStatsService.java      # 書籍統計更新（非同期）
│   ├── BookmarkService.java
│   ├── FavoriteService.java
│   ├── GenreService.java
│   ├── ReviewService.java
│   ├── RoleService.java
│   └── UserService.java
└── util/          # ユーティリティクラス
    ├── JwtUtils.java              # JWT生成・検証
    └── PageableUtils.java         # ページネーション
```

## 重要な設計パターン

### 1. エンティティ設計
- **基底クラス**: `EntityBase` - すべてのエンティティが継承
  - `createdAt`, `updatedAt`, `isDeleted` フィールドを提供
  - 自動タイムスタンプ更新（`@PrePersist`, `@PreUpdate`）
  - 論理削除対応

### 2. 複合主キー設計
- **BookChapterId**: 書籍ID + 章番号の複合主キー
- **BookChapterPageContentId**: 書籍ID + 章番号 + ページ番号の複合主キー
- **@EmbeddedId** アノテーションを使用

### 3. ページネーション戦略
- **ユーティリティ**: `PageableUtils`
- **デフォルト設定**:
  - ページサイズ: 20
  - 最大ページサイズ: 1000（application.properties で設定）
  - ソート: `id.asc`
- **2クエリ戦略**: 大量データの効率的な取得
- **レスポンス**: `PageResponse<T>` で統一
- **1ベースページング**: API レベルで1ベース、内部的に0ベースに変換

### 4. セキュリティ設計
- **JWT認証**: Access Token + Refresh Token（Cookie）
- **カスタムフィルター**: `AuthTokenFilter` - 認証処理の詳細制御
- **パスワード暗号化**: BCrypt
- **CORS**: localhost パターンで設定
- **エンドポイント分類**:
  - 完全パブリック: `/login`, `/signup`, `/logout`, `/refresh-token`
  - GET のみパブリック: `/books/**`, `/genres/**`（詳細は `SecurityEndpointsConfig` を参照）

### 5. 例外処理
- **カスタム例外**: 
  - `NotFoundException`, `BadRequestException`, `ConflictException`
  - `UnauthorizedException`, `ForbiddenException`, `ValidationException`
- **統一エラーレスポンス**: `ErrorResponse` クラス
- **グローバルハンドラ**: `ExceptionControllerAdvice`

### 6. 非同期処理
- **設定**: `AsyncConfig` - 書籍統計更新用
- **サービス**: `BookStatsService` - レビュー・お気に入り統計の非同期更新

## データベース設計

### 主要エンティティ
1. **User** - ユーザー情報（Spring Security UserDetails 実装）
   - Long型ID、email（ユニーク）、password（BCrypt）
   - name、avatarPath、roles（多対多）
2. **Book** - 書籍情報（String型ID、レビュー統計含む）
   - genres（多対多）、reviews、favorites、bookmarks
   - 統計フィールド: reviewCount、averageRating、popularity
3. **BookChapter** - 書籍章情報（複合主キー）
4. **BookChapterPageContent** - 書籍ページコンテンツ（複合主キー）
5. **Genre** - ジャンル（多対多関係）
6. **Review** - レビュー（評価、コメント）
7. **Favorite** - お気に入り
8. **Bookmark** - ブックマーク
9. **Role** - ロール（ADMIN, USER enum）

### データベース設定
```properties
# 設定場所: src/main/resources/application.properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.jpa.properties.hibernate.hbm2dll.create_namespaces=true
server.forward-headers-strategy=native

# ページネーション設定
app.pagination.max-limit=1000
app.pagination.default-limit=20
app.pagination.max-genre-ids=50
app.pagination.max-book-id-length=255
```

### 環境変数
```bash
# データベース
SPRING_DATASOURCE_URL      # データベース接続URL
SPRING_DATASOURCE_USERNAME # データベースユーザー名
SPRING_DATASOURCE_PASSWORD # データベースパスワード

# JWT
SPRING_APP_JWT_SECRET      # JWT署名シークレット（Base64エンコード）
SPRING_APP_JWT_ACCESS_EXPIRATION  # アクセストークン有効期限（秒）
SPRING_APP_JWT_REFRESH_EXPIRATION # リフレッシュトークン有効期限（秒）

# Docker Compose用
DB_URL, DB_USER, DB_PASSWORD, DB_NAME
JWT_SECRET, JWT_ACCESS_EXPIRATION, JWT_REFRESH_EXPIRATION
```

## API 設計

### 認証エンドポイント（AuthController）
- `POST /login` - ログイン（Access Token + Refresh Token Cookie）
- `POST /signup` - ユーザー登録
- `POST /logout` - ログアウト（Cookie削除）
- `POST /refresh-token` - トークンリフレッシュ

### 書籍関連エンドポイント（BookController）
- `GET /books/new-releases` - 最新書籍（10冊）
- `GET /books/search?q=keyword` - タイトル検索
- `GET /books/discover?genreIds=1,2&condition=AND` - ジャンル検索
- `GET /books/{id}` - 書籍詳細
- `GET /books/{id}/toc` - 目次
- `GET /books/{id}/chapters/{chapter}/pages/{page}` - 書籍ページコンテンツ
- `GET /books/{id}/reviews` - レビュー一覧
- `GET /books/{id}/reviews/counts` - レビュー統計
- `GET /books/{id}/favorites/counts` - お気に入り統計

### ユーザー機能エンドポイント
- **BookmarkController**: ブックマーク管理
- **FavoriteController**: お気に入り管理
- **ReviewController**: レビュー管理
- **UserController**: プロフィール管理
- **GenreController**: ジャンル一覧
- **RoleController**: ロール管理
- **AdminUserController**: 管理者用ユーザー管理

### デフォルトパラメータ
```java
// 書籍関連（BookController）
DEFAULT_BOOKS_START_PAGE = "1"
DEFAULT_BOOKS_PAGE_SIZE = "20"
DEFAULT_BOOKS_SORT = "popularity.desc"

// レビュー関連
DEFAULT_REVIEWS_START_PAGE = "1"
DEFAULT_REVIEWS_PAGE_SIZE = "3"
DEFAULT_REVIEWS_SORT = "updatedAt.desc"
```

### ソート可能フィールド（PageableUtils）
```java
// 書籍
BOOK_ALLOWED_FIELDS = ["title", "publicationDate", "reviewCount", "averageRating", "popularity"]

// レビュー
REVIEW_ALLOWED_FIELDS = ["updatedAt", "createdAt", "rating"]

// お気に入り・ブックマーク
FAVORITE_ALLOWED_FIELDS = ["updatedAt", "createdAt"]
BOOKMARK_ALLOWED_FIELDS = ["updatedAt", "createdAt"]
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
4. **セキュリティテスト**: 認証・認可の動作確認

## 開発規約

### 1. ネーミング規約
- **エンティティ**: PascalCase（例: `User`, `BookChapter`）
- **フィールド**: camelCase（例: `createdAt`, `averageRating`）
- **テーブル**: snake_case（例: `users`, `book_chapters`）
- **API エンドポイント**: kebab-case（例: `/new-releases`）
- **複合主キー**: エンティティ名 + "Id"（例: `BookChapterId`）

### 2. パッケージ構成規約
- **Controller**: REST API の責務のみ
- **Service**: ビジネスロジックの実装（インターフェース + 実装クラス）
- **Repository**: データアクセスの抽象化
- **DTO**: API入出力の専用オブジェクト（機能別ディレクトリ分け）
- **Mapper**: Entity ↔ DTO 変換（MapStruct）

### 3. セキュリティ規約
- **認証が必要なエンドポイント**: デフォルト
- **パブリックエンドポイント**: `SecurityEndpointsConfig` で明示的に設定
- **パスワード**: 必ず BCrypt で暗号化
- **JWT**: HttpOnly Cookie でリフレッシュトークン管理
- **CORS**: localhost パターンのみ許可

## 重要な設定ファイル

### 1. `application.properties`
- データベース接続設定（環境変数参照）
- JWT設定（環境変数参照）
- ページネーション設定
- JPA/Hibernate設定
- プロキシヘッダー設定

### 2. `SecurityConfig.java`
- Spring Security設定
- CORS設定（localhost パターン）
- JWT フィルター設定
- エンドポイントアクセス制御
- カスタムログアウト処理

### 3. `SecurityEndpointsConfig.java`
- 完全パブリックエンドポイント定義
- GETのみパブリックエンドポイント定義

### 4. `AuthTokenFilter.java`
- JWT認証フィルター（OncePerRequestFilter継承）
- パブリックエンドポイント判定
- 認証コンテキスト設定
- 詳細ログ出力

### 5. `SwaggerConfig.java`
- OpenAPI設定
- JWT認証スキーム設定（Bearer Token）

### 6. `AsyncConfig.java`
- 非同期処理設定
- 書籍統計更新用スレッドプール

### 7. `docker-compose.yml`
- MySQL 8.0 + アプリケーション環境
- ヘルスチェック設定
- 初期データ投入設定

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
- **2クエリ戦略**: 大量データでのソート順序維持（`PageableUtils.restoreSortOrder()`）
- **lazy loading**: `spring.jpa.open-in-view=false`
- **複合主キー**: `@EmbeddedId` で適切に設計

### 3. ページネーション実装
- **1ベース**: API は1ベースページング
- **0ベース**: JPA Pageable は内部的に0ベース
- **変換**: `PageableUtils.of()` および `PageableUtils.toPageResponse()` で統一
- **ソートフィールド制限**: 許可されたフィールドのみソート可能

### 4. 論理削除パターン
- **フィールド**: `isDeleted` boolean フィールド（EntityBase）
- **クエリ**: `findByIsDeletedFalse()` パターン
- **デフォルト値**: `false`

### 5. 非同期処理
- **統計更新**: レビュー・お気に入り追加時に非同期で書籍統計を更新
- **@Async**: `BookStatsService` で使用

### 6. JWT 管理
- **Access Token**: Authorization ヘッダーで送信
- **Refresh Token**: HttpOnly Cookie で管理
- **ログアウト**: Cookie削除で実装

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
- CORS設定の確認（localhost パターン）
- Cookie設定の確認（HttpOnly、Secure、SameSite）

### 3. データベース接続エラー
- 環境変数の設定確認
- MySQL サーバーの起動確認（Docker環境では `docker-compose logs db`）
- データベース権限の確認
- 初期データ投入の確認

### 4. Docker環境エラー
```bash
# コンテナ状態確認
docker-compose ps

# ログ確認
docker-compose logs app
docker-compose logs db

# 環境変数確認
docker-compose exec app env | grep SPRING
```

## ドキュメント

### Swagger UI
- URL: `http://localhost:8080/swagger-ui.html`
- JWT認証: Bearer Token形式
- 全エンドポイントの詳細仕様を確認可能

### API仕様
- OpenAPI 3 形式で自動生成
- DTOクラスからスキーマ自動生成
- JWT認証スキーム設定済み

## 今後の開発指針

### 1. 新機能追加時
1. DTO クラス作成（リクエスト/レスポンス）
2. エンティティ拡張（必要に応じて）
3. Repository メソッド追加
4. Service インターフェース定義
5. Service 実装クラス作成
6. Controller 層でAPI公開
7. Mapper でEntity/DTO変換
8. セキュリティ設定更新（必要に応じて）
9. テスト作成

### 2. セキュリティ考慮事項
- 新エンドポイントのアクセス制御設定（`SecurityEndpointsConfig`）
- 入力値バリデーション（`@Valid`）
- SQL インジェクション対策（JPA使用により基本対策済み）
- XSS対策（JSON API のため基本対策済み）
- JWT トークンの適切な管理

### 3. パフォーマンス考慮事項
- データベースインデックス設計
- クエリ最適化（N+1問題対策）
- キャッシュ戦略（Spring Cache使用準備済み）
- 非同期処理活用（統計更新等）
- ページネーション制限値の調整

### 4. 運用考慮事項
- ログレベルの調整
- 監視メトリクスの設定
- Docker環境での本番運用準備
- データベースマイグレーション戦略

このドキュメントを参考に、一貫性のある高品質なコードの開発を進めてください。
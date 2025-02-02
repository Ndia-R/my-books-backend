DROP DATABASE IF EXISTS `my-books-db`;
CREATE DATABASE `my-books-db`;

USE `my-books-db`;


DROP TABLE IF EXISTS `books`;
DROP TABLE IF EXISTS `genres`;
DROP TABLE IF EXISTS `book_genres`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `roles`;
DROP TABLE IF EXISTS `user_roles`;
DROP TABLE IF EXISTS `reviews`;
DROP TABLE IF EXISTS `favorites`;
DROP TABLE IF EXISTS `my_lists`;
DROP TABLE IF EXISTS `book_chapters`;
DROP TABLE IF EXISTS `book_pages`;


CREATE TABLE `books` (
  `id` VARCHAR(255) NOT NULL PRIMARY KEY,
  `title` VARCHAR(255) NOT NULL DEFAULT '',
  `description` TEXT NOT NULL,
  `authors` VARCHAR(255) NOT NULL DEFAULT '',
  `publisher` VARCHAR(255) NOT NULL DEFAULT '',
  `published_date` DATE NOT NULL,
  `price` INT NOT NULL DEFAULT 0,
  `page_count` INT NOT NULL DEFAULT 0,
  `isbn` VARCHAR(255) NOT NULL DEFAULT '',
  `image_url` VARCHAR(255) DEFAULT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0
);

CREATE TABLE `genres` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(255) NOT NULL DEFAULT '',
  `description` VARCHAR(255) NOT NULL DEFAULT '',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0
);

CREATE TABLE `book_genres` (
  `book_id` VARCHAR(255) NOT NULL,
  `genre_id` BIGINT NOT NULL,
  PRIMARY KEY (`book_id`, `genre_id`),
  FOREIGN KEY (`book_id`) REFERENCES `books`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`genre_id`) REFERENCES `genres`(`id`) ON DELETE CASCADE
);

CREATE TABLE `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `email` VARCHAR(255) NOT NULL DEFAULT '',
  `password` VARCHAR(255) NOT NULL DEFAULT '',
  `name` VARCHAR(255) NOT NULL DEFAULT '',
  `avatar_url` VARCHAR(255) DEFAULT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0
);

CREATE TABLE `roles` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` VARCHAR(255) NOT NULL DEFAULT '',
  `description` VARCHAR(255) NOT NULL DEFAULT '',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0
);

CREATE TABLE `user_roles` (
  `user_id` BIGINT NOT NULL,
  `role_id` BIGINT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`role_id`) REFERENCES `roles`(`id`) ON DELETE CASCADE
);

CREATE TABLE `reviews` (
  `user_id` BIGINT NOT NULL,
  `book_id` VARCHAR(255) NOT NULL,
  `comment` TEXT NOT NULL,
  `rating` DECIMAL(2, 1) CHECK (`rating` >= 0 AND `rating` <= 5),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`user_id`, `book_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`book_id`) REFERENCES `books`(`id`) ON DELETE CASCADE
);

CREATE TABLE `favorites` (
  `user_id` BIGINT NOT NULL,
  `book_id` VARCHAR(255) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`user_id`, `book_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`book_id`) REFERENCES `books`(`id`) ON DELETE CASCADE
);

CREATE TABLE `my_lists` (
  `user_id` BIGINT NOT NULL,
  `book_id` VARCHAR(255) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`user_id`, `book_id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`book_id`) REFERENCES `books`(`id`) ON DELETE CASCADE
);

CREATE TABLE `book_chapters` (
  `book_id` VARCHAR(255) NOT NULL,
  `chapter_number` BIGINT NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`book_id`, `chapter_number`),
  FOREIGN KEY (`book_id`) REFERENCES `books`(`id`) ON DELETE CASCADE
);

CREATE TABLE `book_pages` (
  `book_id` VARCHAR(255) NOT NULL,
  `chapter_number` BIGINT NOT NULL,
  `page_number` BIGINT NOT NULL,
  `content` TEXT NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`book_id`, `chapter_number`, `page_number`),
  FOREIGN KEY (`book_id`, `chapter_number`) REFERENCES `book_chapters`(`book_id`, `chapter_number`) ON DELETE CASCADE
);

-- データのロード
LOAD DATA INFILE '/docker-entrypoint-initdb.d/books.csv'
INTO TABLE books
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(`id`, `title`, `description`, `authors`, `publisher`, `published_date`, `price`, `page_count`, `isbn`, `image_url`);

INSERT INTO `genres` (`name`, `description`) VALUES
('ミステリー', '謎解きや推理をテーマにした作品'),
('サスペンス', '緊張感や驚きを伴う作品'),
('ロマンス', '恋愛をテーマにした作品'),
('ファンタジー', '魔法や異世界を舞台にした作品'),
('SF', '科学技術や未来をテーマにした作品'),
('ホラー', '恐怖をテーマにした作品'),
('歴史', '歴史的な出来事や人物をテーマにした作品'),
('絵本', '子供向けのイラストが多い本'),
('教科書', '教育機関で使用される教材'),
('専門書', '特定の分野に特化した書籍'),
('研究書', '学術的な研究をまとめた書籍'),
('環境', '自然や環境問題をテーマにした作品'),
('冒険', '冒険や探検をテーマにした作品'),
('図鑑', '特定のテーマに関する情報を集めた書籍'),
('音楽', '音楽に関する書籍'),
('ドラマ', '人間関係や感情を描いた作品'),
('教育', '教育に関する書籍');

LOAD DATA INFILE '/docker-entrypoint-initdb.d/book_genres.csv'
INTO TABLE book_genres
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
(`book_id`, `genre_id`);

INSERT INTO `users` (`name`, `email`, `password`, `avatar_url`) VALUES
('Lars', 'lars@gmail.com', '$2a$10$E7FzFP73ImXXFHUmUUmXtuDrJnp0gZ3Zb3XJluLEW7tfnVmh5FLwC', 'https://localhost/images/avatars/avatar01.png'),
('Nina', 'nina@gmail.com', '$2a$10$E7FzFP73ImXXFHUmUUmXtuDrJnp0gZ3Zb3XJluLEW7tfnVmh5FLwC', 'https://localhost/images/avatars/avatar02.png'),
('Paul', 'paul@gmail.com', '$2a$10$E7FzFP73ImXXFHUmUUmXtuDrJnp0gZ3Zb3XJluLEW7tfnVmh5FLwC', 'https://localhost/images/avatars/avatar03.png'),
('Julia', 'julia@gmail.com', '$2a$10$E7FzFP73ImXXFHUmUUmXtuDrJnp0gZ3Zb3XJluLEW7tfnVmh5FLwC', 'https://localhost/images/avatars/avatar04.png'),
('Lee', 'lee@gmail.com', '$2a$10$E7FzFP73ImXXFHUmUUmXtuDrJnp0gZ3Zb3XJluLEW7tfnVmh5FLwC', 'https://localhost/images/avatars/avatar05.png'),
('Lili', 'lili@gmail.com', '$2a$10$E7FzFP73ImXXFHUmUUmXtuDrJnp0gZ3Zb3XJluLEW7tfnVmh5FLwC', 'https://localhost/images/avatars/avatar06.png'),
('Steve', 'steve@gmail.com', '$2a$10$E7FzFP73ImXXFHUmUUmXtuDrJnp0gZ3Zb3XJluLEW7tfnVmh5FLwC', 'https://localhost/images/avatars/avatar07.png'),
('Anna', 'anna@gmail.com', '$2a$10$E7FzFP73ImXXFHUmUUmXtuDrJnp0gZ3Zb3XJluLEW7tfnVmh5FLwC', 'https://localhost/images/avatars/avatar08.png'),
('Law', 'law@gmail.com', '$2a$10$E7FzFP73ImXXFHUmUUmXtuDrJnp0gZ3Zb3XJluLEW7tfnVmh5FLwC', 'https://localhost/images/avatars/avatar09.png'),
('Alisa', 'alisa@gmail.com', '$2a$10$E7FzFP73ImXXFHUmUUmXtuDrJnp0gZ3Zb3XJluLEW7tfnVmh5FLwC', 'https://localhost/images/avatars/avatar10.png');

INSERT INTO `roles` (`name`, `description`) VALUES
('ROLE_ADMIN', '管理者権限'),
('ROLE_USER', 'ユーザー権限');

INSERT INTO `user_roles` (`user_id`, `role_id`) VALUES
(1, 2),
(2, 2),
(3, 2),
(4, 2),
(5, 2),
(6, 2),
(7, 2),
(8, 2),
(9, 2),
(10, 2),
(3, 1),
(4, 1);

INSERT INTO `reviews` (`user_id`, `book_id`, `comment`, `rating`) VALUES
(1, 'afcIMuetDuzj', '知識の宝庫で、読み終える頃には少し賢くなった気がした。', 4.5),
(2, 'afcIMuetDuzj', '人生観が変わるほどの深い洞察が詰まっていました。', 3.0),
(3, 'afcIMuetDuzj', '読む手が止まらないほど引き込まれた。', 3.5),
(4, 'afcIMuetDuzj', '心に響く言葉が何度も胸を打った。', 5.0),
(5, 'afcIMuetDuzj', '言葉の美しさに何度もページをめくり直した。', 4.0),
(6, 'afcIMuetDuzj', '想像力をかき立てられる素晴らしいストーリーだった。', 4.5),
(7, 'afcIMuetDuzj', '感動しました。何度も読み直したいと思いました。', 3.0),
(8, 'afcIMuetDuzj', '登場人物に感情移入しすぎて泣いてしまった。', 5.0),
(9, 'afcIMuetDuzj', '終わるのが惜しいほど楽しかった。', 4.5),
(10, 'afcIMuetDuzj', '感動的な結末に、読後の余韻が心地よかった。', 3.5),
(1, '9UizZw491wye', '読み進むにつれドンドン引き込まれていきました。', 3.5),
(2, '9UizZw491wye', '首を長くして待っていました。非常に楽しかったです。', 3.0),
(3, '9UizZw491wye', '読んでいる間、時間を忘れるほど夢中になれました。', 3.0),
(4, '9UizZw491wye', '物語の展開が巧妙で、予想を超える展開が続いて面白かったです。', 3.0),
(1, 'pDYIwtdahwkp', '私もこんな経験をしたいと思いました。', 5.0);

INSERT INTO `favorites` (`user_id`, `book_id`) VALUES
(1, 'afcIMuetDuzj'),
(2, 'afcIMuetDuzj'),
(3, 'afcIMuetDuzj'),
(4, 'afcIMuetDuzj'),
(4, 'pDYIwtdahwkp'),
(5, 'pDYIwtdahwkp'),
(6, 'pDYIwtdahwkp'),
(3, '9UizZw491wye'),
(3, 'ln5NiMJq02V7');

INSERT INTO `my_lists` (`user_id`, `book_id`) VALUES
(4, '3BndPAiEFnjB'),
(4, '3gNJnRcrwrUH'),
(3, 'v21pjIlzDua1');

INSERT INTO `book_chapters` (`book_id`, `chapter_number`, `title`) VALUES
('afcIMuetDuzj', 0, ''),
('afcIMuetDuzj', 1, '第一章：湖畔の招待状'),
('afcIMuetDuzj', 2, '第二章：運命の出会い'),
('afcIMuetDuzj', 3, '第三章：舞踏会の奇跡'),
('afcIMuetDuzj', 4, '第四章：消えゆく光'),
('afcIMuetDuzj', 5, '第五章：新たな誓い');

INSERT INTO `book_pages` (`book_id`, `chapter_number`, `page_number`, `content`) VALUES
('afcIMuetDuzj', 0, 1, '月が湖面に銀の道を描く夜、静寂の森の奥深くで、ワニたちの秘密の舞踏会が開かれる。年に一度、満月の夜にだけ現れるこの神秘の宴は、選ばれた者だけが参加を許される。'),
('afcIMuetDuzj', 1, 2, '若きワニのルカは、幼いころからこの舞踏会の伝説を聞かされて育った。しかし、実際にその場に足を踏み入れた者は少なく、語られる話のほとんどは夢物語のようだった。月の舞踏会とは、一体どのようなものなのか。その幻想的な響きに、ルカは幼いころから憧れを抱いていた。\n\nある日、湖の畔を散歩していると、微かに光るものが目に入った。近づいてみると、青い蓮の葉の上に、一枚の紙が置かれていた。それは銀色の文字で「月の舞踏会へようこそ」と記された招待状だった。光を帯びた紙は、まるで月の欠片のように輝いている。\n\nルカは驚きと共にそれを手に取った。招待状は、信じられないほど滑らかな手触りで、触れると心が温かくなるような気がした。なぜ自分が選ばれたのか、考えても答えは出なかったが、胸の奥に湧き上がる興奮を抑えることはできなかった。\n\nその夜、ルカは星空の下で静かに湖を見つめた。湖面には月が映り、ゆらゆらと揺れていた。その光景が、まるでこれから訪れる運命を象徴しているかのように感じられた。月は優しく光を投げかけ、まるでルカを舞踏会へ誘うように湖面を照らしていた。'),
('afcIMuetDuzj', 1, 3, '翌日、ルカは森の長老のもとを訪れ、舞踏会について尋ねた。長老はゆっくりと目を閉じ、深い呼吸をしたあと、静かに語り始めた。「月の舞踏会は、心を持つ者のみが招かれる。お前が選ばれたということは、運命がお前に試練を与えたのかもしれん」\n\n「試練……？」ルカは長老の言葉を反芻した。\n\n「そうじゃ。舞踏会に足を踏み入れた者は、ただ踊るだけではない。そこでは、心の奥底に眠る想いが試されるのじゃ」\n\n長老は遠い目をしながら続けた。「かつて、舞踏会に参加した者の中には、愛を見つけた者もおれば、悲しみを抱えた者もいた。だが、いずれもこの宴が人生にとって大切な瞬間になったことは確かじゃ」\n\nルカはその言葉の意味を考えながら、招待状を大切に胸に抱いた。そして、舞踏会の夜が訪れるのを、今か今かと待ちわびた。'),
('afcIMuetDuzj', 2, 4, '舞踏会の夜、ルカは湖のほとりへと向かった。湖は鏡のように月の光を映し、その周囲には神秘的な青白い霧が漂っていた。湖畔にはすでに多くのワニたちが集まっていた。彼らの鱗は月光を浴びて淡く輝き、まるでこの世のものとは思えないほど幻想的な光景が広がっていた。\n\nルカは緊張しながらその輪の中へと足を踏み入れた。すると、遠くの方でひときわ美しく輝くワニの姿が目に入った。彼女の名はセレナ。透き通るような銀色の鱗を持ち、瞳は夜空の星を映したように輝いていた。その姿は、まるで月の化身のようだった。\n\n「今夜は特別な夜よ」\n\nセレナは微笑みながらルカに手を差し出した。その瞬間、ルカの胸の奥にかすかな震えが走った。彼の鼓動は高鳴り、時間が止まったかのように感じられた。迷うことなくルカは彼女の手を取り、二人はゆっくりと舞踏の輪の中へと入っていった。\n\n最初はぎこちない動きだったが、セレナが優しくリードしてくれた。彼女の手は驚くほど柔らかく温かかった。ルカの緊張は徐々に解け、二人のステップはしだいに調和を生み出し始めた。湖の水面には、月の光と二人の姿が映り込み、ゆらめく波紋とともに美しい模様を描いていた。'),
('afcIMuetDuzj', 2, 5, '他のワニたちもそれぞれのパートナーと踊り、湖全体がまるで幻想の世界へと変わっていく。水のさざめきと夜風の旋律が、音楽のように響き渡る。ルカはこの夜が永遠に続けばいいと心から願った。\n\n「ここでは、過去も未来も関係ない。私たちは今、この瞬間だけを生きているの」\n\nセレナはルカの耳元で囁いた。その声は心の奥深くに響き、ルカの胸を熱くした。彼はセレナの手を強く握りしめ、踊り続けた。言葉を交わさずとも、彼らの間には確かな絆が生まれつつあった。\n\n夜が更けるにつれ、湖面の霧はますます濃くなり、まるで夢の世界へと足を踏み入れたかのようだった。ワニたちは次第に踊りに没頭し、時間の流れを忘れていった。ルカはふと、セレナの瞳を覗き込んだ。その深い輝きの中には、何か秘密が隠されているように感じられた。\n\n「セレナ……君は、なぜこの舞踏会に？」\n\nルカが尋ねると、セレナは少し寂しそうに微笑んだ。「私は、月に仕える一族のワニなの。だから、満月の夜にだけこの場所へ来ることができるの」\n\n「満月の夜だけ……？」ルカは思わず息を呑んだ。'),
('afcIMuetDuzj', 2, 6, 'セレナは静かに頷いた。「この舞踏会は、月の光によって開かれる。でも、その光が消えれば、私は再び湖の奥深くへ戻らなければならないの」\n\nルカは彼女の言葉を飲み込みながら、胸が締めつけられるような感覚を覚えた。この夜が終われば、彼女とは離れ離れになってしまうのだろうか。\n\nしかし、ルカはその思いを押し殺し、今はただこの瞬間を大切にしようと決めた。彼はセレナの手をそっと引き寄せ、静かに言った。「今は……この夜を楽しもう」\n\nセレナの瞳が驚いたように揺れ、そして優しく微笑んだ。「ええ、そうね」\n\n二人は再び踊り始めた。湖の水面はまるで鏡のように彼らを映し出し、月の光が二人の周りを優しく包み込んでいた。ルカは、今この瞬間だけは、彼女とずっと一緒にいられるのだと信じたかった。\n\nその夜、彼らの心は一つになり、運命が新たな軌跡を描き始めたのだった。'),
('afcIMuetDuzj', 3, 7, 'ワニたちのダンスは、水面を揺らす優雅な波紋となり、月の光がそれに呼応するかのように輝きを増した。音もなく進むダンスの中で、ルカはセレナに心を奪われていく。\n\n「ここでは、時間が止まるの」\n\nセレナの言葉どおり、この夜の間だけ、ワニたちは自由に心を通わせることができる。しかし、夜明けとともに舞踏会は終わり、それぞれの生活へと戻らなければならなかった。\n\nルカはその言葉の意味を考えながら、彼女の手をぎゅっと握った。セレナの温もりが、彼の胸の中に深く刻まれていく。彼女の微笑みが、この場所の魔法そのものであるかのように感じられた。'),
('afcIMuetDuzj', 3, 8, 'しかし、彼の心には小さな不安が芽生え始めていた。この夜が終わったら、彼はまた孤独に戻るのだろうか？セレナと過ごした時間は夢のように消えてしまうのか？\n\n「セレナ……この夜が終わっても、君に会う方法はないの？」\n\nルカが問いかけると、セレナは一瞬言葉を飲み込んだ。しかし、彼の真剣なまなざしを見つめると、静かに微笑んだ。\n\n「ルカ、私は月の光によってここに来ることができるの。だから……あなたが月を信じてくれるなら、私はまたここに戻ってこられるわ」\n\n彼女の言葉には確かな優しさと切なさが込められていた。ルカはその意味を深く理解した。彼女は、自らの運命に縛られているのだ。それでも、彼女はこの夜を楽しもうとしている。\n\n「だったら……僕は月を信じる。君を信じるよ、セレナ」\n\n彼は力強く言い、彼女の手を握る。その瞬間、月の光が二人を包み込み、まるで二人だけの世界が広がったかのようだった。'),
('afcIMuetDuzj', 3, 9, '舞踏会はますます盛り上がり、ワニたちは次々と踊りに身を委ねていく。湖の水面には、光の粒が浮かび、まるで星々が降り注いでいるかのようだった。ルカとセレナは、その美しさに酔いしれながら、静かに踊り続けた。\n\n「ルカ……ありがとう」\n\nセレナがそっと囁く。彼女の声には、感謝と切なさが混じっていた。\n\n「セレナ、僕は……」\n\nルカが何かを言おうとした瞬間、遠くの空に一筋の流れ星が走った。その光が湖に反射し、まるで二人の未来を示すように揺らめいた。ルカはその輝きを見上げながら、心に誓った。\n\nこの夜がどれだけ儚くても、この出会いは永遠のものにしよう。\n\n彼らは夜明けまで踊り続けた。時が止まったように、ただひたすらに。\n\nルカは願った。この時間が永遠に続くようにと。'),
('afcIMuetDuzj', 4, 10, '夜が更けるにつれ、月はゆっくりと傾き始めた。そして、舞踏会が終わる時が近づいてくる。湖の水面に映る月の光が揺らめき、ワニたちは一匹また一匹と静かに去っていった。しかし、ルカはその場を動けなかった。セレナの手を握りしめたまま、彼は心の奥からこみ上げる感情を抑えきれずにいた。\n\n「朝が来ても、僕たちはまた会えるの？」\n\nルカの問いかけに、セレナはわずかに微笑んだ。しかし、その笑顔の奥には悲しみが滲んでいた。\n\n「この夜を信じて……」\n\nそう囁くと、彼女の体はゆっくりと透明になり始めた。ルカは目を見開いた。彼女の手の感触が消えていく。彼の目の前で、セレナは淡い月光とともに霧のように消えていったのだ。\n\n「セレナ！！」\n\nルカは叫んだ。彼女の名を何度も呼び、手を伸ばした。しかし、彼が触れることができたのは冷たい湖の風だけだった。湖面には静かな波紋が広がり、すべてが夢であったかのように静寂に包まれた。'),
('afcIMuetDuzj', 4, 11, '彼女はどこへ行ってしまったのか？ なぜ突然消えてしまったのか？\n\nそのとき、ルカの後ろから低い声が響いた。\n\n「お前も気づいたか……？」\n\nルカが振り返ると、そこには長老のワニ、エルダーが立っていた。彼は湖の舞踏会を何十年も見守ってきた賢者だった。\n\n「彼女は……どこへ？」\n\n震える声でルカが尋ねると、エルダーは深いため息をついた。\n\n「セレナは……もともとこの世界の住人ではないのだよ」\n\nルカの心臓が跳ねた。\n\n「それは、どういうことですか？」\n\nエルダーは湖面を見つめながら語り始めた。\n\n「昔、この湖には月の使者がいた。彼らは満月の夜にだけ現れ、地上の者と交流を持つことが許されていた。しかし、それは決して永遠には続かない掟だった。舞踏会が終わるとともに、彼らは月へと帰らなければならないのだ」\n\nルカの心臓が強く打った。'),
('afcIMuetDuzj', 4, 12, '「セレナが……月の使者？」\n\n「そうだ。彼女は湖に住むワニではなく、月の民の一人だった。お前と出会ったのも偶然ではない。彼女は、この湖で自分を待ってくれる者を探していたのかもしれない」\n\nルカは震えた。彼の心の中に生まれた愛は、彼女の運命によって引き裂かれてしまうのか。彼は拳を握りしめた。\n\n「でも……彼女は僕に言ったんです。また会えるって」\n\nエルダーは静かに頷いた。\n\n「確かに、満月の夜になれば彼女は再びここへ来ることができる。しかし、それはお前の想いの強さにかかっているのだ」\n\n「僕の……想い？」\n\n「そうだ。月の使者が地上に留まるためには、強い絆が必要だ。もし、お前が彼女を心から信じ続けるなら、彼女は再びこの湖に降り立つことができるかもしれない」\n\nルカは湖を見つめた。月の光が優しく揺らめき、まるでセレナの面影を映しているようだった。彼の胸に強い決意が生まれた。\n\n「僕は、彼女を信じます。何があっても、満月の夜を待ちます」\n\nエルダーは静かに微笑んだ。\n\n「ならば、お前に試練が与えられるだろう。次の満月までに、心を揺らがせぬよう強く持ち続けるのだ。そうすれば、月はお前の願いを聞き入れてくれるかもしれない」\n\nルカは深く息を吸った。湖の冷たい風が、彼の心に決意を刻み込んだ。\n\n「セレナ……僕は君を待つよ。何があっても……」\n\nそう誓った夜、湖の奥からかすかな光が浮かび上がった。月の使者が残した、わずかな奇跡のように。彼はその場を離れることができず、ただ彼女の影を探し続けた。'),
('afcIMuetDuzj', 5, 13, 'ルカは満月の夜を待ち続けた。日が昇り、日が沈むたびに、彼の心はセレナへの想いを募らせていった。彼女が本当に戻ってくるのか、それともこれは儚い夢だったのか。不安と希望が入り混じりながらも、彼は誓いを守り続けた。\n\nそして、運命の夜が訪れた。\n\n湖のほとりに立つルカの前で、水面が光を帯び始めた。その光は月と呼応するように輝き、やがて湖の中心からセレナの姿が浮かび上がった。\n\n「ルカ……」\n\n彼女の声は、夜風に溶けるように優しく響いた。ルカは目を見開き、彼女へと駆け寄った。\n\n「セレナ！ 本当に戻ってきたんだね！」\n\n彼は彼女の手を取った。その温もりが、確かにここにいることを証明していた。\n\n「私はあなたの想いに導かれて戻ってきたの。でも……私は決めなければならないの」\n\n「決める？」\n\n「私は月へ戻るか、ここに残るかの選択を迫られているの」\n\n湖の光が強くなり、セレナの姿を包み込むように揺らめいた。'),
('afcIMuetDuzj', 5, 14, '「ルカ、私は……」\n\n彼女の目が揺れ動いていた。戸惑い、恐れ、そして希望。すべての感情が交錯していた。\n\n「私は、あなたと共に生きたい。でも、もし私がこの世界に残るなら、二度と月の光に触れることはできないの」\n\nルカの心臓が強く打った。\n\n「それは……どういうこと？」\n\nセレナは静かに湖を見つめた。月の光が彼女の足元に淡く差し込み、その身体を透かしていた。\n\n「私は月の使者。私の存在は月と結びついている。でも、もし私がこの湖に残るなら、私は……」\n\n彼女は言葉を飲み込んだ。\n\n「私は、ただのワニになる」\n\nルカは息をのんだ。\n\n「それって……どういうこと？」\n\n「私は、今までの記憶を失い、この湖の住人として生きることになるの」\n\n静寂が広がった。\n\n「君のことも、全部忘れるってこと？」\n\nセレナは小さく頷いた。\n\n「でも、私はあなたと生きたい。あなたが私を愛してくれるなら……たとえ記憶がなくなっても、私はあなたのそばにいたいの」\n\nルカは拳を握りしめた。セレナのそばにいられるなら、それだけでいいと思うべきなのか。でも、彼女が自分のことを忘れてしまうのなら、それは本当に幸せと言えるのか。'),
('afcIMuetDuzj', 5, 15, '「……セレナ、本当にそれでいいの？」\n\n「怖い。でも、あなたのそばにいたい。だから……選ぶのはあなた」\n\nルカは震える手で彼女の頬に触れた。\n\n「僕は……」\n\nそのとき、湖の奥から新たな光が差し込んだ。エルダーが静かに現れた。\n\n「決断の時だ。ルカ、お前が彼女を心から愛し、彼女のすべてを受け入れるなら……月の掟を超える方法がある」\n\n「方法が？」\n\nエルダーは湖に手をかざし、湖面が波打った。\n\n「お前が彼女を愛し続け、どんな時もその想いを忘れなければ……セレナは月の使者としてではなく、一個の存在として生きることができる。記憶を失わずに」\n\n「本当か？」\n\n「ただし、それにはお前の魂も試される。お前が彼女を信じ続け、どんな困難にも負けない覚悟が必要だ」\n\nルカはセレナを見つめた。\n\n「セレナ、僕は君と共に生きたい。君の記憶が消えるなんて、そんなの耐えられない。でも、もし僕の想いが君をつなぎとめるなら……」\n\nセレナは微笑んだ。'),
('afcIMuetDuzj', 5, 16, '「なら……試してみる？」\n\nルカは深く頷いた。\n\n「どんな試練でも、僕は乗り越える。君と一緒にいるためなら」\n\nその瞬間、湖の光が大きく揺らぎ、二人を包み込んだ。世界が眩しい光に満たされ、すべてが溶け合うような感覚に襲われた。\n\n気が付くと、ルカは湖のほとりに倒れていた。\n\n「ルカ？」\n\n優しい声が聞こえた。顔を上げると、そこには変わらぬ姿のセレナがいた。\n\n「セレナ……？」\n\n「私、まだ覚えてる……！」\n\nルカの胸に喜びが広がった。彼はセレナを抱きしめた。\n\n「やった……！」\n\n湖の水面には、二匹のワニが寄り添う影が映っていた。月は穏やかに輝き、二人の未来を祝福するように照らしていた。\n\n彼らの愛は、月の掟すら超えたのだ。\n\n──ワニと月の舞踏会は、終わりを迎えた。\n\nしかし、それは二人の新たな物語の始まりでもあった。');

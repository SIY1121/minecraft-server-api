# The Gusyatto Craft API
Minecraft Bukkit Serverに簡単なRest APIを生やすプラグイン。（身内用）

- bukkit:1.15.2-R0.1-SNAPSHOT
- spigot-api:1.15.2-R0.1-SNAPSHOT
- ktor:1.3.2

# エンドポイント
動作確認用を除いて Basic認証が必要
ユーザー名とパスワードは初回起動は両方 "dummy"
起動後に生成されるコンフィグファイルを弄って再起動すれば変更される。


## GET /
動作確認用

### 戻り値
```json
{
  "msg": "The GusyattoCraft Server API"
}
```

## GET /users
ログイン中ユーザーを返す

### 戻り値
```json
["User1", "User2", "User3"]
```

## PUT /users/{username}/kick
指定されたユーザーを切断する

### body
```json
{
  "msg": "キックされたユーザーに表示されるメッセージ"
}
```

### 戻り値
```json
{
  "msg": "success"
}
```
指定したユーザーがオフラインだと404が帰る

## PUT /users/{username}/tell
指定したユーザーにメッセージを送信
### body
```json
{
  "msg": "送信するメッセージ"
}
```

### 戻り値
```json
{
  "msg": "success"
}
```
指定したユーザーがオフラインだと404が帰る

## PUT /server/say
サーバー全員にメッセージを送信
### body
```json
{
  "msg": "送信するメッセージ"
}
```

### 戻り値
```json
{
  "msg": "success"
}
```

## PUT /server/shutdown
鯖を落とす
### body
無し

### 戻り値
```json
{
  "msg": "success"
}
```

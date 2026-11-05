## io-aux

`io.readall (filename)`

读文件全部内容，失败返回 `nil` 和错误信息。

---

`io.writeall (filename, data [, atomic])`

把 `data` 字符串全部写入文件，成功返回 `true`。

如果 `atomic` 是 `true` 表示是原子写入，
会创建 `.tmp` 临时文件写，写完重命名避免中断导致文件损坏。
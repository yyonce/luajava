## io-aux

`io.readall (filename)`

Reads the entire contents of a file. Returns `nil` and an error message on failure.

---

`io.writeall (filename, data [, atomic])`

Writes the string `data` to a file, returning `true` on success.

If `atomic` is `true`, the write is atomic: a `.tmp` file is created and renamed after writing, avoiding corruption if the write is interrupted.
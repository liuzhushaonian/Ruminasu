# Ruminasu
Ruminasu（露米娜丝）——追求体验的Android本地漫画阅读APP

## 数据库

漫画表 t_comic

id int(唯一标识，但存入前需要查找是否有title一致)
title string（漫画名，但几乎是唯一标识）
hide int（是否被隐藏起来）
path string（路径）
book string(封面路径，缓存目录，第一次存入)


章节表 t_chapter

id int（主键）
name string（名称，第一话之类的，去掉第或数字前的字）
read int（是否已经阅读过，是1 否-1）
book string（封面，默认第一页，初次存入时缓存）
c_order int （顺序）
type string （属于哪个分类）
comic_id int （漫画id，外键）

书签表 t_bookmark

id int（主键）
comic_id int（漫画id）
chapter_id int（章节id）
page int（具体页数）

历史表 t_history

id int（主键）
comic_id（漫画id，唯一）
chapter_id（章节id）
page int（具体页数）

路径表 t_path

id int
path string

## 待续
# Описание

Приложение представляет собой RSS-ридер. Оно включает в себя следующие компоненты:
- UI для отображения списка каналов с возможностью их добавления/удаления (**AlertDialog**)
- UI для отображения списка статей (**ListView** + **BaseAdapter**)
- UI для отображения контента статьи (**WebView**)
- Загрузка и парсинг данных из сети в **IntentService** (**URLConnection**, **SAX** и **AsyncTask**)
- Хранение всех данных в БД **SQLite** (**ContentProvider**)
- Загрузка данных из БД с помощью **AsyncTaskLoader**

Приложение предназначено для Android 4.4 (API 20) и будет работать на смартфонах с версией не ниже
Android 4.0.3 (API 15).

### Скриншоты

![Фиды отсутствуют](screenshots/empty_feed_list.png)
![Окно добавления фида](screenshots/adding_feed.png)
![Список фидов](screenshots/feed_list.png)
![Список новостей](screenshots/post_list.png)
![Описание новости](screenshots/post_description.png)
![Полный текст новости на странице сайта](screenshots/post_content.png)
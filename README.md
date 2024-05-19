# SubsctiptionManager
# Создание MVP
## На данном этапе приложение должно иметь следующий функционал:
- Пользователь может зарегистрироваться и авторизоваться в               приложении.
- Добавлять подписки, удалять их, изменять(например, изменение срока истечения), получать информацию о текущих подписках
- Создавать, удалять, изменять категории.
- Добавлять подписки в категории.
- Получать отчеты по подпискам(по конкретным категориям или в общем)
- Экспорт отчетов в csv и pdf. Будет реализована библиотека для создания отчета в csv или подобных форматах. 
	
## Организация ролей(admin, user):

- Функциональность admin’a приложения:
- У админа будет функциональность, которой нет у user. 
- Добавлять подписки в каталог цифровых подписок
- Удалять пользователя
- Просматривать информацию о всех юзерах

# Как запустить?

## Для локального запуска приложения нужно настроить следующие переменные окружения:

### Email рассылка
Создать аккаунт почтового клиента от имени которого будет рассылка сообщений
- `${EMAIL_USERNAME}`
- `${EMAIL_PASSWORD}`
### tg bot + security
- `${BOT_TOKEN}` - API бота, получить можно с помощью BotFather, предварительно требуется создать его
- `${JWT_SECRET_KEY}` - JWT секретный ключ, длина > 32 симвлов в UTF-8
- `${JWT_EXPIRATION_MS}` - время жизни jwt токена в ms
- `${CRYPTO_SECRET_KEY}` - секретный ключ для шифрования и дешифрования сообщений
### google calendar
Необходимо зарегистрировать приложение в google cloud для возможности работы с API Google календаря, https://developers.google.com/calendar/api/guides/overview?hl=ru
Также файл с кредами credentials.json необходимо поместить в папку с ресурсами
- `${GOOGLE_CLIENT_ID}` - id администратора приложения
- `${GOOGLE_CLIENT_SECRET}` - ключ от приложения

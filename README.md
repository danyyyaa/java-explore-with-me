# Explore With Me

## Стек: Java 11, Spring Boot, Spring Data JPA, PostgreSQL, Docker, Maven

Свободное время — ценный ресурс. Ежедневно мы планируем, как его потратить — куда и с кем сходить. Сложнее всего в таком планировании поиск информации и переговоры. Нужно учесть много деталей: какие намечаются мероприятия, свободны ли в этот момент друзья, как всех пригласить и где собраться.
Explore With Me — афиша. В этой афише можно предложить какое-либо событие от выставки до похода в кино и собрать компанию для участия в нём.

---

## Микросервисная архитектура
Приложение состоит из 2 сервисов:
- stats-service - часть приложения, которая собирает, хранит и отдает по запросу статистику по просмотрам.
 API: https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-main-service-spec.json
- main-service - основная часть приложения, в которой происходит вся логика приложения.
 API: https://raw.githubusercontent.com/yandex-praktikum/java-explore-with-me/main/ewm-main-service-spec.json


**Жизненный цикл события включает в себя несколько этапов:**
- Создание
- Ожидание публикации. Событие переходит в состояние ожидания публикации сразу после его создания
- Публикация. Администратор переводит событие в это состояние
- Отмена публикации. Событие переходит в это состояние в двух случаях. Первый - если администратор решил, что оно не должно быть опубликовано. Второй - если инициатор события решил отменить его на стадии ожидания публикации

Основной сервис и сервис статистики сохраняют и загружают данные из разных баз данных. Взаимодействие сервисов в момент сохранения информации о запросе к API осуществляется с помощью клиента сервиса статистики

---
 Схема БД main-service:
![Untitled](https://github.com/danyyyaa/java-explore-with-me/assets/118910569/1ace2c75-c47e-44ba-8649-23c166b07d92)


 Схема БД stats-service:

![Untitled (1)](https://github.com/danyyyaa/java-explore-with-me/assets/118910569/9222814b-4826-4903-9968-2581c48cabf5)

---
## Установка и запуск проекта
### 1 вариант:
Необходимо настроенная система виртуализации, установленный Docker Desktop(скачать и установить можно с официального сайта https://www.docker.com/products/docker-desktop/)

1. Клонируйте репозиторий проекта на свою локальную машину:
   ```
   git clone git@github.com:danyyyaa/java-explore-with-me.git
   ```
2. Запустите командную строку и перейдите в корень директории с проектом.
3. Соберите проект 
   ```
   mvn clean package
   ```
4. Введите следующую команду, которая подготовит и запустит приложение на вашей локальной машине
   ```
   $  docker-compose up
   ```
5. Приложение будет запущено на порту 8080. Вы можете открыть свой веб-браузер и перейти по адресу `http://localhost:8080`, чтобы получить доступ к приложению ExploreWithMe.

### 2 вариант:

1. Установите Java Development Kit (JDK) версии 11 или выше, если у вас его еще нет.
2. Установите PostgreSQL и создайте базу данных для проекта.
3. Клонируйте репозиторий проекта на свою локальную машину:

   ```
   git clone git@github.com:danyyyaa/java-explore-with-me.git
   ```

4. Откройте проект в вашей IDE.
5. Настройте файл `application.properties`, расположенный в директории `src/main/resources`, чтобы указать данные для подключения к вашей базе данных PostgreSQL.
6. Запустите приложение, выполнив следующую команду в корневой директории проекта:

   ```
   mvn spring-boot:run
   ```

7. Приложение будет запущено на порту 8080. Вы можете открыть свой веб-браузер и перейти по адресу `http://localhost:8080`, чтобы получить доступ к приложению Explore With Me.





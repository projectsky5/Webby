# Webby

Java-приложение для обработки логов переводов между пользователями.

---

## Требования

- Java 8+
- Только стандартная библиотека Java SE

---

## Использованные технологии

- Java 21
- Maven
- Lombok
- JUnit5 & Mockito + AssertJ
- Docker

---

## Функционал

- Обрабатывает `.log`-файлы с операциями пользователей (баланс, перевод, снятие).
- Объединяет все операции для каждого пользователя.
- Формирует персональные файлы `user001.log`, `user002.log` и т.п.
- Добавляет финальный рассчитанный баланс по каждому пользователю.
- Поддерживает запуск локально и в Docker-контейнере.

---

## Запуск без Docker

### 1. Сборка проекта

Убедитесь, что у вас установлен Maven и JDK 21+  
Затем выполните:

```bash
mvn clean package
```

После сборки будет создан JAR-файл с зависимостями:

```
target/app.jar
```

---

### 2. Подготовьте директорию с логами

Создайте папку, например `logs`, и поместите туда `.log`-файлы в формате:

```
[2025-05-10 09:00:22] user001 balance inquiry 1000.00
[2025-05-10 09:05:44] user001 transferred 100.00 to user002
```

---

### 3. Запуск приложения

#### На macOS / Linux:

```bash
java -jar target/app.jar ./logs
```

#### На Windows:

```cmd
java -jar target\app.jar logs
```

После выполнения в папке `logs` появится директория `transactions_by_users/` с результатами.

---

## Запуск через Docker

### 1. Сборка Docker-образа

```bash
docker build -t transfer-log-app .
```

---

### 2. Подготовьте папку с логами

Пример:

```
Webby/
│
├── logs/
│   ├── log1.log
│   └── log2.log
├── Dockerfile
└── target/
    └── app.jar
```

---

### 3. Запуск контейнера

#### macOS / Linux

```bash
docker run --rm -v "$(pwd)/logs:/logs" transfer-log-app /logs
```

	•	$(pwd)/logs — локальная папка с .log файлами
	•	/logs — путь внутри контейнера
	•	transfer-log-app — имя образа
	•	/logs (после имени образа) — аргумент в main() = args[0]

#### Windows CMD

```cmd
docker run --rm -v %cd%\logs:/logs transfer-log-app /logs
```

После завершения работы, в локальной папке `logs` появится `transactions_by_users/` с файлами каждого пользователя.

---

## Пример выходного файла

`user001.log`:

```
[2025-05-10 09:00:22] user001 balance inquiry 1000.00
[2025-05-10 09:05:44] user001 transferred 100.00 to user002
[2025-05-10 10:03:23] user001 received 990.00 from user002
[2025-05-10 11:00:03] user001 final balance 1890.00
```

---

## Автор

telegram: [@xtenzyy](https://t.me/xtenzyy)

email: [projectsky5@yandex.ru](mailto:projectsky5@yandex.ru)

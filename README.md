# Telegram Bot for Personal Use

## Overview

This project is a Telegram bot designed for personal use, offering features such as note-taking, viewing notes, accessing chatGPT for AI-generated responses, and checking bus schedules. The bot is built using `Java 22`, with a `PostgreSQL 16` database for data storage, `Jsoup` for web scraping bus schedules, `Spring Boot` for application development and management, and the `OpenAI API` for integrating chatGPT.

## Features

1. **Note-taking**: Add, view, and manage personal notes.
2. **ChatGPT Integration**: Ask questions and receive AI-generated responses using the OpenAI API.
3. **Bus Schedules**: View bus schedules by scraping relevant websites.

## Technology Stack

- **Java 22**: The core programming language used to develop the bot.
- **Spring Boot**: Framework used to build and run the application, providing ease of development and various out-of-the-box features.
- **PostgreSQL 16**: The relational database management system for storing notes and other data.
- **Jsoup**: A Java library for web scraping, used to parse and extract bus schedule information from websites.
- **OpenAI API**: Used for integrating chatGPT to provide AI-generated responses.
- **Telegram API**: The bot interacts with users through the Telegram platform.

## Usage

### Adding a Note
- Use the command `/addnote` to add a new note.

### Viewing Notes
- Use the command `/mynotes` to display all your notes.

### Asking chatGPT
- Use the command `/gpt` to get a response from chatGPT.

### Checking Bus Schedules
- Use the command `/busschedule` to view the schedule for a specific bus route.

## Project Structure

- `src/main/java` - Contains the Java source files.
- `src/main/resources` - Contains configuration files and resources.
- `src/main/resources/application.properties` - Configuration file for the bot.

## Tools and Libraries

### Java 22
- The main programming language used for the project, leveraging the latest features and improvements.

### Spring Boot
- A framework that simplifies the development of new Spring applications, providing default configurations and reducing the amount of boilerplate code required.

### PostgreSQL 16
- A powerful, open-source object-relational database system providing robust data storage and retrieval capabilities.

### Jsoup
- A Java library for working with real-world HTML. Provides a very convenient API for extracting and manipulating data, used for scraping bus schedules.

### OpenAI API
- Provides access to chatGPT, allowing the bot to generate AI responses to user queries.

### Maven
- A build automation tool used for managing project dependencies and building the project.

## Contributing

1. Fork the project.
2. Create your feature branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a pull request.

## License

This project is licensed under the MIT License. See the LICENSE file for details.

## Acknowledgements

- [Telegram API](https://core.telegram.org/bots/api)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [PostgreSQL](https://www.postgresql.org/)
- [Jsoup](https://jsoup.org/)
- [OpenAI](https://www.openai.com/) for chatGPT
- [OpenAI API](https://beta.openai.com/docs/api-reference/introduction) for integrating chatGPT


For any questions or feedback, please contact [@dema_koder]

package com.UnderUpb.backendUnderUpb;

import com.UnderUpb.backendUnderUpb.entity.*;
import com.UnderUpb.backendUnderUpb.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LevelRepository levelRepository;
    private final QuestionRepository questionRepository;
    private final CharacterRepository characterRepository;
    private final EnemyRepository enemyRepository;

    @Override
    public void run(String... args) throws Exception {
        init();
    }

    private void init() {
        // Create root user
        if (userRepository.findAll().isEmpty()) {
            User rootUser = User.builder()
                    .name("root")
                    .lifePoints(10)
                    .score(0)
                    .currentLevel(1)
                    .inventory("{}")
                    .build();
            userRepository.save(rootUser);
            log.info("Root user created successfully");

            // Create sample levels
            Level level1 = Level.builder()
                    .name("Forest Temple")
                    .description("A mysterious forest filled with ancient ruins")
                    .requiredXp(100)
                    .orderIndex(1)
                    .build();
            levelRepository.save(level1);

            Level level2 = Level.builder()
                    .name("Ice Castle")
                    .description("A frozen castle at the top of the mountains")
                    .requiredXp(200)
                    .orderIndex(2)
                    .build();
            levelRepository.save(level2);

            Level level3 = Level.builder()
                    .name("Dark Dungeon")
                    .description("A treacherous dungeon filled with dark magic")
                    .requiredXp(300)
                    .orderIndex(3)
                    .build();
            levelRepository.save(level3);

            log.info("Sample levels created");

            // Create sample characters
            CharacterEntity knight = CharacterEntity.builder()
                    .name("Knight")
                    .description("A brave knight with high defense")
                    .abilities("{\"strength\": 8, \"defense\": 10, \"speed\": 5}")
                    .requiredLevel(1)
                    .build();
            characterRepository.save(knight);

            CharacterEntity mage = CharacterEntity.builder()
                    .name("Mage")
                    .description("A powerful mage with high magic power")
                    .abilities("{\"magic\": 10, \"wisdom\": 9, \"defense\": 4}")
                    .requiredLevel(2)
                    .build();
            characterRepository.save(mage);

            CharacterEntity rogue = CharacterEntity.builder()
                    .name("Rogue")
                    .description("A swift rogue with high speed")
                    .abilities("{\"speed\": 10, \"agility\": 9, \"strength\": 6}")
                    .requiredLevel(3)
                    .build();
            characterRepository.save(rogue);

            log.info("Sample characters created");

            // Create sample questions for level 1
            Question q1 = Question.builder()
                    .text("What is the capital of France?")
                    .level(1)
                    .optionsJson("[\"Paris\", \"London\", \"Berlin\", \"Madrid\"]")
                    .answer("Paris")
                    .description("A basic geography question")
                    .build();
            questionRepository.save(q1);

            Question q2 = Question.builder()
                    .text("What is 2 + 2?")
                    .level(1)
                    .optionsJson("[\"3\", \"4\", \"5\", \"6\"]")
                    .answer("4")
                    .description("A simple math question")
                    .build();
            questionRepository.save(q2);

            Question q3 = Question.builder()
                    .text("What is the largest planet in our solar system?")
                    .level(1)
                    .optionsJson("[\"Mars\", \"Saturn\", \"Jupiter\", \"Neptune\"]")
                    .answer("Jupiter")
                    .description("A space science question")
                    .build();
            questionRepository.save(q3);

            // Create sample questions for level 2
            Question q4 = Question.builder()
                    .text("What is the chemical symbol for Gold?")
                    .level(2)
                    .optionsJson("[\"Go\", \"Gd\", \"Au\", \"Ag\"]")
                    .answer("Au")
                    .description("A chemistry question")
                    .build();
            questionRepository.save(q4);

            Question q5 = Question.builder()
                    .text("Who wrote Romeo and Juliet?")
                    .level(2)
                    .optionsJson("[\"Mark Twain\", \"William Shakespeare\", \"Jane Austen\", \"Charles Dickens\"]")
                    .answer("William Shakespeare")
                    .description("A literature question")
                    .build();
            questionRepository.save(q5);

            log.info("Sample questions created");

            // Create sample enemies
            Enemy goblin = Enemy.builder()
                    .name("Goblin")
                    .damage(5)
                    .totalLife(20)
                    .level(1)
                    .behaviorJson("{\"type\": \"aggressive\", \"speed\": 6}")
                    .build();
            enemyRepository.save(goblin);

            Enemy skeleton = Enemy.builder()
                    .name("Skeleton Knight")
                    .damage(8)
                    .totalLife(30)
                    .level(2)
                    .behaviorJson("{\"type\": \"tactical\", \"speed\": 5}")
                    .build();
            enemyRepository.save(skeleton);

            Enemy dragon = Enemy.builder()
                    .name("Fire Dragon")
                    .damage(15)
                    .totalLife(100)
                    .level(3)
                    .behaviorJson("{\"type\": \"boss\", \"speed\": 4, \"spells\": [\"fireball\", \"tailswipe\"]}")
                    .build();
            enemyRepository.save(dragon);

            log.info("Sample enemies created");
            log.info("Data initialization completed successfully!");
        } else {
            log.info("Database already populated, skipping initialization");
        }
    }

}

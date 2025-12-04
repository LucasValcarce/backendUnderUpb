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
    private final AnswerRepository answerRepository;
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
                    .build();
            userRepository.save(rootUser);
            log.info("Root user created successfully");

            // Create sample levels
            Level level1 = Level.builder()
                    .name("Forest Temple")
                    .description("A mysterious forest filled with ancient ruins")
                    .orderIndex(1)
                    .build();
            levelRepository.save(level1);

            Level level2 = Level.builder()
                    .name("Ice Castle")
                    .description("A frozen castle at the top of the mountains")
                    .orderIndex(2)
                    .build();
            levelRepository.save(level2);

            Level level3 = Level.builder()
                    .name("Dark Dungeon")
                    .description("A treacherous dungeon filled with dark magic")
                    .orderIndex(3)
                    .build();
            levelRepository.save(level3);

            log.info("Sample levels created");

            // Create sample characters
            CharacterEntity knight = CharacterEntity.builder()
                    .name("Knight")
                    .description("A brave knight with high defense")
                    .build();
            characterRepository.save(knight);

            CharacterEntity mage = CharacterEntity.builder()
                    .name("Mage")
                    .description("A powerful mage with high magic power")
                    .build();
            characterRepository.save(mage);

            CharacterEntity rogue = CharacterEntity.builder()
                    .name("Rogue")
                    .description("A swift rogue with high speed")
                    .build();
            characterRepository.save(rogue);

            log.info("Sample characters created");

            // Create sample questions for level 1
            Question q1 = Question.builder()
                    .text("What is the capital of France?")
                    .level(1)
                    .description("A basic geography question")
                    .build();
            questionRepository.save(q1);
            
            // Create answers for q1
            Answers a1_1 = Answers.builder()
                    .question(q1)
                    .text("Paris")
                    .isCorrect(true)
                    .explanation("Paris is the capital and most populous city of France")
                    .build();
            answerRepository.save(a1_1);
            
            Answers a1_2 = Answers.builder()
                    .question(q1)
                    .text("London")
                    .isCorrect(false)
                    .explanation("London is the capital of the United Kingdom")
                    .build();
            answerRepository.save(a1_2);
            
            Answers a1_3 = Answers.builder()
                    .question(q1)
                    .text("Berlin")
                    .isCorrect(false)
                    .explanation("Berlin is the capital of Germany")
                    .build();
            answerRepository.save(a1_3);
            
            Answers a1_4 = Answers.builder()
                    .question(q1)
                    .text("Madrid")
                    .isCorrect(false)
                    .explanation("Madrid is the capital of Spain")
                    .build();
            answerRepository.save(a1_4);

            Question q2 = Question.builder()
                    .text("What is 2 + 2?")
                    .level(1)
                    .description("A simple math question")
                    .build();
            questionRepository.save(q2);
            
            // Create answers for q2
            Answers a2_1 = Answers.builder()
                    .question(q2)
                    .text("3")
                    .isCorrect(false)
                    .explanation("2 + 2 is not 3")
                    .build();
            answerRepository.save(a2_1);
            
            Answers a2_2 = Answers.builder()
                    .question(q2)
                    .text("4")
                    .isCorrect(true)
                    .explanation("2 + 2 equals 4")
                    .build();
            answerRepository.save(a2_2);
            
            Answers a2_3 = Answers.builder()
                    .question(q2)
                    .text("5")
                    .isCorrect(false)
                    .explanation("2 + 2 is not 5")
                    .build();
            answerRepository.save(a2_3);
            
            Answers a2_4 = Answers.builder()
                    .question(q2)
                    .text("6")
                    .isCorrect(false)
                    .explanation("2 + 2 is not 6")
                    .build();
            answerRepository.save(a2_4);

            Question q3 = Question.builder()
                    .text("What is the largest planet in our solar system?")
                    .level(1)
                    .description("A space science question")
                    .build();
            questionRepository.save(q3);
            
            // Create answers for q3
            Answers a3_1 = Answers.builder()
                    .question(q3)
                    .text("Mars")
                    .isCorrect(false)
                    .explanation("Mars is smaller than Jupiter")
                    .build();
            answerRepository.save(a3_1);
            
            Answers a3_2 = Answers.builder()
                    .question(q3)
                    .text("Saturn")
                    .isCorrect(false)
                    .explanation("Saturn is smaller than Jupiter")
                    .build();
            answerRepository.save(a3_2);
            
            Answers a3_3 = Answers.builder()
                    .question(q3)
                    .text("Jupiter")
                    .isCorrect(true)
                    .explanation("Jupiter is the largest planet in our solar system")
                    .build();
            answerRepository.save(a3_3);
            
            Answers a3_4 = Answers.builder()
                    .question(q3)
                    .text("Neptune")
                    .isCorrect(false)
                    .explanation("Neptune is smaller than Jupiter")
                    .build();
            answerRepository.save(a3_4);

            // Create sample questions for level 2
            Question q4 = Question.builder()
                    .text("What is the chemical symbol for Gold?")
                    .level(2)
                    .description("A chemistry question")
                    .build();
            questionRepository.save(q4);
            
            // Create answers for q4
            Answers a4_1 = Answers.builder()
                    .question(q4)
                    .text("Go")
                    .isCorrect(false)
                    .explanation("Go is not the symbol for Gold")
                    .build();
            answerRepository.save(a4_1);
            
            Answers a4_2 = Answers.builder()
                    .question(q4)
                    .text("Gd")
                    .isCorrect(false)
                    .explanation("Gd is the symbol for Gadolinium")
                    .build();
            answerRepository.save(a4_2);
            
            Answers a4_3 = Answers.builder()
                    .question(q4)
                    .text("Au")
                    .isCorrect(true)
                    .explanation("Au is the chemical symbol for Gold, from its Latin name 'Aurum'")
                    .build();
            answerRepository.save(a4_3);
            
            Answers a4_4 = Answers.builder()
                    .question(q4)
                    .text("Ag")
                    .isCorrect(false)
                    .explanation("Ag is the symbol for Silver")
                    .build();
            answerRepository.save(a4_4);

            Question q5 = Question.builder()
                    .text("Who wrote Romeo and Juliet?")
                    .level(2)
                    .description("A literature question")
                    .build();
            questionRepository.save(q5);
            
            // Create answers for q5
            Answers a5_1 = Answers.builder()
                    .question(q5)
                    .text("Mark Twain")
                    .isCorrect(false)
                    .explanation("Mark Twain wrote other famous works like Tom Sawyer")
                    .build();
            answerRepository.save(a5_1);
            
            Answers a5_2 = Answers.builder()
                    .question(q5)
                    .text("William Shakespeare")
                    .isCorrect(true)
                    .explanation("William Shakespeare wrote Romeo and Juliet in the late 16th century")
                    .build();
            answerRepository.save(a5_2);
            
            Answers a5_3 = Answers.builder()
                    .question(q5)
                    .text("Jane Austen")
                    .isCorrect(false)
                    .explanation("Jane Austen wrote Pride and Prejudice and other novels")
                    .build();
            answerRepository.save(a5_3);
            
            Answers a5_4 = Answers.builder()
                    .question(q5)
                    .text("Charles Dickens")
                    .isCorrect(false)
                    .explanation("Charles Dickens wrote A Tale of Two Cities and other works")
                    .build();
            answerRepository.save(a5_4);

            log.info("Sample questions created");

            // Create sample enemies
            Enemy goblin = Enemy.builder()
                    .name("Goblin")
                    .damage(5)
                    .totalLife(20)
                    .level(1)
                    .build();
            enemyRepository.save(goblin);

            Enemy skeleton = Enemy.builder()
                    .name("Skeleton Knight")
                    .damage(8)
                    .totalLife(30)
                    .level(2)
                    .build();
            enemyRepository.save(skeleton);

            Enemy dragon = Enemy.builder()
                    .name("Fire Dragon")
                    .damage(15)
                    .totalLife(100)
                    .level(3)
                    .build();
            enemyRepository.save(dragon);

            log.info("Sample enemies created");
            log.info("Data initialization completed successfully!");
        } else {
            log.info("Database already populated, skipping initialization");
        }
    }

}

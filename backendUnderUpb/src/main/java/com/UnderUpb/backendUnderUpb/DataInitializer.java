package com.UnderUpb.backendUnderUpb;

import com.UnderUpb.backendUnderUpb.entity.*;
import com.UnderUpb.backendUnderUpb.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LevelRepository levelRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CharacterRepository characterRepository;

    @Override
    public void run(String... args) throws Exception {
        init();
    }

    private void init() {
        // Create root user if database is empty
        if (userRepository.findAll().isEmpty()) {
            User rootUser = User.builder()
                    .name("root")
                    .studentCode(69058)
                    .lifePoints(100)
                    .maxLifePoints(100)
                    .score(0)
                    .currentLevel(1)
                    .build();
            userRepository.save(rootUser);
            // Usuarios con score alto (para top rankings)
            User player1 = User.builder()
                    .name("ShadowNinja")
                    .studentCode(75001)
                    .lifePoints(85)
                    .maxLifePoints(100)
                    .score(9850)
                    .currentLevel(25)
                    .build();

            User player2 = User.builder()
                    .name("CyberMage")
                    .studentCode(75002)
                    .lifePoints(100)
                    .maxLifePoints(100)
                    .score(11250)
                    .currentLevel(30)
                    .build();

            User player3 = User.builder()
                    .name("QuantumKnight")
                    .studentCode(75003)
                    .lifePoints(42)
                    .maxLifePoints(100)
                    .score(8750)
                    .currentLevel(22)
                    .build();

// Usuarios con score medio
            User player4 = User.builder()
                    .name("NeoPhantom")
                    .studentCode(75004)
                    .lifePoints(60)
                    .maxLifePoints(100)
                    .score(5200)
                    .currentLevel(15)
                    .build();

            User player5 = User.builder()
                    .name("AzureStriker")
                    .studentCode(75005)
                    .lifePoints(95)
                    .maxLifePoints(100)
                    .score(4300)
                    .currentLevel(12)
                    .build();

            User player6 = User.builder()
                    .name("CrystalWarden")
                    .studentCode(75006)
                    .lifePoints(30)
                    .maxLifePoints(100)
                    .score(3100)
                    .currentLevel(10)
                    .build();

// Usuarios con score bajo
            User player7 = User.builder()
                    .name("BlazeRunner")
                    .studentCode(75007)
                    .lifePoints(75)
                    .maxLifePoints(100)
                    .score(1250)
                    .currentLevel(5)
                    .build();

            User player8 = User.builder()
                    .name("FrostSeeker")
                    .studentCode(75008)
                    .lifePoints(50)
                    .maxLifePoints(100)
                    .score(850)
                    .currentLevel(4)
                    .build();

// Usuarios con score muy alto (para asegurar top positions)
            User player9 = User.builder()
                    .name("TitanSlayer")
                    .studentCode(75009)
                    .lifePoints(100)
                    .maxLifePoints(100)
                    .score(15500)
                    .currentLevel(35)
                    .build();

            User player10 = User.builder()
                    .name("DragonMaster")
                    .studentCode(75010)
                    .lifePoints(88)
                    .maxLifePoints(100)
                    .score(14200)
                    .currentLevel(33)
                    .build();

// Usuarios con score intermedio
            User player11 = User.builder()
                    .name("StarVoyager")
                    .studentCode(75011)
                    .lifePoints(65)
                    .maxLifePoints(100)
                    .score(6800)
                    .currentLevel(18)
                    .build();

            User player12 = User.builder()
                    .name("MythicHunter")
                    .studentCode(75012)
                    .lifePoints(25)
                    .maxLifePoints(100)
                    .score(2500)
                    .currentLevel(8)
                    .build();

            List<User> users = Arrays.asList(player1, player2, player3, player4, player5,
                    player6, player7, player8, player9, player10,
                    player11, player12);
            userRepository.saveAll(users);
            log.info("Root user created successfully");

            // Create Level 1: Forest Temple (Tutorial)
            Level level1 = Level.builder()
                    .name("Templo del Bosque")
                    .description("Un misterioso bosque lleno de ruinas antiguas. Aprende los conceptos básicos respondiendo preguntas.")
                    .orderIndex(1)
                    .build();
            level1 = levelRepository.save(level1);
            log.info("Level 1 created: {}", level1.getId());

            // Level 1 Questions - Filosofía
            Question q1_1 = Question.builder()
                    .text("¿Quién es el autor de la frase 'pienso, luego existo'?")
                    .level(level1)
                    .description("Una pregunta clásica sobre filosofía")
                    .build();
            q1_1 = questionRepository.save(q1_1);

            List<Answers> answers1_1 = new ArrayList<>();
            answers1_1.add(Answers.builder()
                    .question(q1_1)
                    .text("Platón")
                    .isCorrect(false)
                    .explanation("Platón fue un filósofo griego antiguo, pero no es el autor de esta frase.")
                    .build());
            answers1_1.add(Answers.builder()
                    .question(q1_1)
                    .text("Galileo Galilei")
                    .isCorrect(false)
                    .explanation("Galileo fue un astrónomo y físico, no el autor de esta frase.")
                    .build());
            answers1_1.add(Answers.builder()
                    .question(q1_1)
                    .text("René Descartes")
                    .isCorrect(true)
                    .explanation("¡Correcto! René Descartes es el filósofo francés que acuñó esta famosa frase en el siglo XVII.")
                    .build());
            answers1_1.add(Answers.builder()
                    .question(q1_1)
                    .text("Sócrates")
                    .isCorrect(false)
                    .explanation("Sócrates fue un filósofo griego, pero vivió mucho antes de Descartes.")
                    .build());
            answerRepository.saveAll(answers1_1);
            q1_1.setAnswers(answers1_1);
            questionRepository.save(q1_1);

            Question q1_2 = Question.builder()
                    .text("¿Cuál es el libro más vendido del mundo después de la Biblia?")
                    .level(level1)
                    .description("Una pregunta sobre literatura clásica")
                    .build();
            q1_2 = questionRepository.save(q1_2);

            List<Answers> answers1_2 = new ArrayList<>();
            answers1_2.add(Answers.builder()
                    .question(q1_2)
                    .text("Don Quijote de la Mancha")
                    .isCorrect(true)
                    .explanation("¡Correcto! Don Quijote de Cervantes es el segundo libro más vendido de todos los tiempos.")
                    .build());
            answers1_2.add(Answers.builder()
                    .question(q1_2)
                    .text("La Biblia")
                    .isCorrect(false)
                    .explanation("La Biblia es el primero, no el segundo.")
                    .build());
            answers1_2.add(Answers.builder()
                    .question(q1_2)
                    .text("El Principito")
                    .isCorrect(false)
                    .explanation("El Principito es un libro muy popular, pero no está en el segundo lugar.")
                    .build());
            answers1_2.add(Answers.builder()
                    .question(q1_2)
                    .text("La Odisea")
                    .isCorrect(false)
                    .explanation("La Odisea es un clásico, pero no es el segundo más vendido.")
                    .build());
            answerRepository.saveAll(answers1_2);
            q1_2.setAnswers(answers1_2);
            questionRepository.save(q1_2);

            Question q1_3 = Question.builder()
                    .text("¿Cuál es la capital de Hungría?")
                    .level(level1)
                    .description("Una pregunta de geografía europea")
                    .build();
            q1_3 = questionRepository.save(q1_3);

            List<Answers> answers1_3 = new ArrayList<>();
            answers1_3.add(Answers.builder()
                    .question(q1_3)
                    .text("Viena")
                    .isCorrect(false)
                    .explanation("Viena es la capital de Austria, no de Hungría.")
                    .build());
            answers1_3.add(Answers.builder()
                    .question(q1_3)
                    .text("Praga")
                    .isCorrect(false)
                    .explanation("Praga es la capital de República Checa.")
                    .build());
            answers1_3.add(Answers.builder()
                    .question(q1_3)
                    .text("Budapest")
                    .isCorrect(true)
                    .explanation("¡Correcto! Budapest es la hermosa capital de Hungría, conocida por sus puentes sobre el Danubio.")
                    .build());
            answers1_3.add(Answers.builder()
                    .question(q1_3)
                    .text("Estambul")
                    .isCorrect(false)
                    .explanation("Estambul es la ciudad más grande de Turquía.")
                    .build());
            answerRepository.saveAll(answers1_3);
            q1_3.setAnswers(answers1_3);
            questionRepository.save(q1_3);

            Question q1_4 = Question.builder()
                    .text("Si P = M + N, ¿cuál de las siguientes fórmulas es correcta?")
                    .level(level1)
                    .description("Una pregunta de matemáticas básicas")
                    .build();
            q1_4 = questionRepository.save(q1_4);

            List<Answers> answers1_4 = new ArrayList<>();
            answers1_4.add(Answers.builder()
                    .question(q1_4)
                    .text("M = P + N")
                    .isCorrect(false)
                    .explanation("Esto sería incorrecto. Sería P = M + N, no P + N.")
                    .build());
            answers1_4.add(Answers.builder()
                    .question(q1_4)
                    .text("N = P + M")
                    .isCorrect(false)
                    .explanation("Esto es incorrecto. N no puede ser igual a P + M.")
                    .build());
            answers1_4.add(Answers.builder()
                    .question(q1_4)
                    .text("M = P - N")
                    .isCorrect(true)
                    .explanation("¡Correcto! Si P = M + N, entonces M = P - N.")
                    .build());
            answers1_4.add(Answers.builder()
                    .question(q1_4)
                    .text("N = P / M")
                    .isCorrect(false)
                    .explanation("Esto no es una manipulación correcta de la ecuación original.")
                    .build());
            answerRepository.saveAll(answers1_4);
            q1_4.setAnswers(answers1_4);
            questionRepository.save(q1_4);

            Question q1_5 = Question.builder()
                    .text("¿Cuál es el término para una palabra o frase que se lee igual al revés que hacia adelante?")
                    .level(level1)
                    .description("Una pregunta de lingüística")
                    .build();
            q1_5 = questionRepository.save(q1_5);

            List<Answers> answers1_5 = new ArrayList<>();
            answers1_5.add(Answers.builder()
                    .question(q1_5)
                    .text("Anagrama")
                    .isCorrect(false)
                    .explanation("Un anagrama es una palabra formada por las letras de otra, pero en diferente orden.")
                    .build());
            answers1_5.add(Answers.builder()
                    .question(q1_5)
                    .text("Tautónimo")
                    .isCorrect(false)
                    .explanation("Un tautónimo es un nombre científico formado por dos partes idénticas.")
                    .build());
            answers1_5.add(Answers.builder()
                    .question(q1_5)
                    .text("Ambigrama")
                    .isCorrect(false)
                    .explanation("Un ambigrama es un texto que puede ser leído de múltiples formas, pero no necesariamente al revés.")
                    .build());
            answers1_5.add(Answers.builder()
                    .question(q1_5)
                    .text("Palíndromo")
                    .isCorrect(true)
                    .explanation("¡Correcto! Un palíndromo es una palabra, frase o número que se lee igual hacia adelante y hacia atrás. Ejemplos: radar, oso, nivel.")
                    .build());
            answerRepository.saveAll(answers1_5);
            q1_5.setAnswers(answers1_5);
            questionRepository.save(q1_5);

            log.info("Level 1 questions created");

            // Create Level 2: Ice Castle
            Level level2 = Level.builder()
                    .name("Castillo de Hielo")
                    .description("Un castillo congelado en la cima de las montañas. Responde preguntas para derrotar enemigos con tus disparos.")
                    .orderIndex(2)
                    .build();
            level2 = levelRepository.save(level2);
            log.info("Level 2 created: {}", level2.getId());

            // Level 2 Questions - Historia y Ciencia
            Question q2_1 = Question.builder()
                    .text("¿Cuál es la capital de Francia?")
                    .level(level2)
                    .description("Una pregunta de geografía")
                    .build();
            q2_1 = questionRepository.save(q2_1);

            List<Answers> answers2_1 = new ArrayList<>();
            answers2_1.add(Answers.builder()
                    .question(q2_1)
                    .text("Londres")
                    .isCorrect(false)
                    .explanation("Londres es la capital de Reino Unido.")
                    .build());
            answers2_1.add(Answers.builder()
                    .question(q2_1)
                    .text("París")
                    .isCorrect(true)
                    .explanation("¡Correcto! París es la hermosa capital de Francia, conocida como la ciudad de la luz.")
                    .build());
            answers2_1.add(Answers.builder()
                    .question(q2_1)
                    .text("Berlín")
                    .isCorrect(false)
                    .explanation("Berlín es la capital de Alemania.")
                    .build());
            answers2_1.add(Answers.builder()
                    .question(q2_1)
                    .text("Madrid")
                    .isCorrect(false)
                    .explanation("Madrid es la capital de España.")
                    .build());
            answerRepository.saveAll(answers2_1);
            q2_1.setAnswers(answers2_1);
            questionRepository.save(q2_1);

            Question q2_2 = Question.builder()
                    .text("¿Quién pintó la Mona Lisa?")
                    .level(level2)
                    .description("Una pregunta sobre arte renacentista")
                    .build();
            q2_2 = questionRepository.save(q2_2);

            List<Answers> answers2_2 = new ArrayList<>();
            answers2_2.add(Answers.builder()
                    .question(q2_2)
                    .text("Picasso")
                    .isCorrect(false)
                    .explanation("Picasso fue un pintor moderno español, no renacentista.")
                    .build());
            answers2_2.add(Answers.builder()
                    .question(q2_2)
                    .text("Leonardo da Vinci")
                    .isCorrect(true)
                    .explanation("¡Correcto! Leonardo da Vinci pintó la Mona Lisa, una de las obras maestras más famosas del mundo.")
                    .build());
            answers2_2.add(Answers.builder()
                    .question(q2_2)
                    .text("Van Gogh")
                    .isCorrect(false)
                    .explanation("Van Gogh fue un pintor postimpresionista holandés.")
                    .build());
            answers2_2.add(Answers.builder()
                    .question(q2_2)
                    .text("Salvador Dalí")
                    .isCorrect(false)
                    .explanation("Dalí fue un pintor surrealista español del siglo XX.")
                    .build());
            answerRepository.saveAll(answers2_2);
            q2_2.setAnswers(answers2_2);
            questionRepository.save(q2_2);

            Question q2_3 = Question.builder()
                    .text("¿Cuántos lados tiene un hexágono?")
                    .level(level2)
                    .description("Una pregunta de geometría")
                    .build();
            q2_3 = questionRepository.save(q2_3);

            List<Answers> answers2_3 = new ArrayList<>();
            answers2_3.add(Answers.builder()
                    .question(q2_3)
                    .text("5")
                    .isCorrect(false)
                    .explanation("Un pentágono tiene 5 lados.")
                    .build());
            answers2_3.add(Answers.builder()
                    .question(q2_3)
                    .text("6")
                    .isCorrect(true)
                    .explanation("¡Correcto! Un hexágono tiene 6 lados.")
                    .build());
            answers2_3.add(Answers.builder()
                    .question(q2_3)
                    .text("7")
                    .isCorrect(false)
                    .explanation("Un heptágono tiene 7 lados.")
                    .build());
            answers2_3.add(Answers.builder()
                    .question(q2_3)
                    .text("8")
                    .isCorrect(false)
                    .explanation("Un octágono tiene 8 lados.")
                    .build());
            answerRepository.saveAll(answers2_3);
            q2_3.setAnswers(answers2_3);
            questionRepository.save(q2_3);

            Question q2_4 = Question.builder()
                    .text("¿Qué gas respiran las plantas?")
                    .level(level2)
                    .description("Una pregunta de biología")
                    .build();
            q2_4 = questionRepository.save(q2_4);

            List<Answers> answers2_4 = new ArrayList<>();
            answers2_4.add(Answers.builder()
                    .question(q2_4)
                    .text("Oxígeno")
                    .isCorrect(false)
                    .explanation("Las plantas producen oxígeno, pero lo que respiran es dióxido de carbono.")
                    .build());
            answers2_4.add(Answers.builder()
                    .question(q2_4)
                    .text("Dióxido de carbono (CO2)")
                    .isCorrect(true)
                    .explanation("¡Correcto! Las plantas respiran CO2 durante el día para realizar la fotosíntesis.")
                    .build());
            answers2_4.add(Answers.builder()
                    .question(q2_4)
                    .text("Nitrógeno")
                    .isCorrect(false)
                    .explanation("El nitrógeno es importante para las plantas, pero no es lo que respiran.")
                    .build());
            answers2_4.add(Answers.builder()
                    .question(q2_4)
                    .text("Hidrógeno")
                    .isCorrect(false)
                    .explanation("El hidrógeno es un componente del agua, pero no es lo que las plantas respiran.")
                    .build());
            answerRepository.saveAll(answers2_4);
            q2_4.setAnswers(answers2_4);
            questionRepository.save(q2_4);

            Question q2_5 = Question.builder()
                    .text("¿Cuál es el océano más grande?")
                    .level(level2)
                    .description("Una pregunta de geografía física")
                    .build();
            q2_5 = questionRepository.save(q2_5);

            List<Answers> answers2_5 = new ArrayList<>();
            answers2_5.add(Answers.builder()
                    .question(q2_5)
                    .text("Océano Atlántico")
                    .isCorrect(false)
                    .explanation("El Atlántico es el segundo océano más grande.")
                    .build());
            answers2_5.add(Answers.builder()
                    .question(q2_5)
                    .text("Océano Pacífico")
                    .isCorrect(true)
                    .explanation("¡Correcto! El Océano Pacífico es el más grande del mundo.")
                    .build());
            answers2_5.add(Answers.builder()
                    .question(q2_5)
                    .text("Océano Índico")
                    .isCorrect(false)
                    .explanation("El Índico es el tercer océano más grande.")
                    .build());
            answers2_5.add(Answers.builder()
                    .question(q2_5)
                    .text("Océano Ártico")
                    .isCorrect(false)
                    .explanation("El Ártico es el océano más pequeño.")
                    .build());
            answerRepository.saveAll(answers2_5);
            q2_5.setAnswers(answers2_5);
            questionRepository.save(q2_5);

            log.info("Level 2 questions created");

            // Create Level 3: Dark Dungeon (Pong Game)
            Level level3 = Level.builder()
                    .name("Calabozo Oscuro")
                    .description("Un calabozo peligroso lleno de magia oscura. Juega al pong contra un enemigo poderoso para escapar.")
                    .orderIndex(3)
                    .build();
            level3 = levelRepository.save(level3);
            log.info("Level 3 created: {}", level3.getId());

            // Level 3 Question
            Question q3_1 = Question.builder()
                    .text("¿Cuál es el resultado de 15 × 7?")
                    .level(level3)
                    .description("Una pregunta de multiplicación")
                    .build();
            q3_1 = questionRepository.save(q3_1);

            List<Answers> answers3_1 = new ArrayList<>();
            answers3_1.add(Answers.builder()
                    .question(q3_1)
                    .text("100")
                    .isCorrect(false)
                    .explanation("15 × 7 no es 100.")
                    .build());
            answers3_1.add(Answers.builder()
                    .question(q3_1)
                    .text("105")
                    .isCorrect(true)
                    .explanation("¡Correcto! 15 × 7 = 105.")
                    .build());
            answers3_1.add(Answers.builder()
                    .question(q3_1)
                    .text("110")
                    .isCorrect(false)
                    .explanation("15 × 7 no es 110.")
                    .build());
            answers3_1.add(Answers.builder()
                    .question(q3_1)
                    .text("95")
                    .isCorrect(false)
                    .explanation("15 × 7 no es 95.")
                    .build());
            answerRepository.saveAll(answers3_1);
            q3_1.setAnswers(answers3_1);
            questionRepository.save(q3_1);

            log.info("Level 3 questions created");

            // Create sample characters
            CharacterEntity knight = CharacterEntity.builder()
                    .name("Caballero")
                    .description("Un valiente caballero con alta defensa")
                    .build();
            characterRepository.save(knight);

            CharacterEntity mage = CharacterEntity.builder()
                    .name("Mago")
                    .description("Un poderoso mago con alto poder mágico")
                    .build();
            characterRepository.save(mage);

            CharacterEntity rogue = CharacterEntity.builder()
                    .name("Pícaro")
                    .description("Un veloz pícaro con alta velocidad")
                    .build();
            characterRepository.save(rogue);

            log.info("Sample characters created");
            log.info("All data initialization completed successfully!");

        } else {
            log.info("Database already initialized, skipping data initialization");
        }
    }

}

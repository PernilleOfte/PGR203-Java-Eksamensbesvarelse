package no.kristiania.database;

import no.kristiania.controllers.ProjectTaskOptionsController;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectTaskDaoTest {
    private ProjectTaskDao taskDao;
    private Random random = new Random();


    //kjører før alle testene
    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().clean();
        Flyway.configure().dataSource(dataSource).load().migrate();
        taskDao = new ProjectTaskDao(dataSource);
    }

    @Test
    void shouldRetrieveAllTaskProperties() throws SQLException {
        taskDao.insert(exampleTask());
        taskDao.insert(exampleTask());
        ProjectTask task = exampleTask();
        taskDao.insert(task);
        assertThat(task).hasNoNullFieldsOrProperties();

        assertThat(taskDao.retrieve(task.getId()))
                .usingRecursiveComparison()
                .isEqualTo(task);

    }

    @Test
    void shouldListAllTasks() throws SQLException {
        ProjectTask task1 = exampleTask();
        ProjectTask task2 = exampleTask();
        taskDao.insert(task1);
        taskDao.insert(task2);
        assertThat(taskDao.list())
                .extracting(ProjectTask::getName)
                .contains(task1.getName(), task2.getName());
    }



    @Test
    void shouldReturnProjectTaskAsOptions() throws SQLException {
        ProjectTaskOptionsController controller = new ProjectTaskOptionsController(taskDao);
        ProjectTask projectTask = exampleTask();
        taskDao.insert(projectTask);

        assertThat(controller.getBody())
                .contains("<option value=" + projectTask.getId() + ">" + projectTask.getName() + "</option>");
    }



    public static ProjectTask exampleTask() {
        ProjectTask task = new ProjectTask();
        task.setName(exampleTaskName());
        task.setStatus(exampleTaskStatus());
        return task;
    }

    private static String exampleTaskStatus() {
        String[] options = {"To do", "To do", "To do"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }

    public static String exampleTaskName() {
        String[] options = {"Skoleoppgave", "Vaske", "Rydde"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
}



}


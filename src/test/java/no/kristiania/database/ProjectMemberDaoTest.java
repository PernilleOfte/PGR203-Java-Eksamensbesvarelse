package no.kristiania.database;

import no.kristiania.controllers.ProjectMemberOptionsController;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectMemberDaoTest {
    private ProjectMemberDao projectMemberDao;
    private static Random random = new Random();
    private ProjectTaskDao projectTaskDao;

    @BeforeEach
    void setUp() {
            JdbcDataSource dataSource = new JdbcDataSource();
            dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
            Flyway.configure().dataSource(dataSource).load().migrate();
            projectMemberDao = new ProjectMemberDao(dataSource);
            projectTaskDao = new ProjectTaskDao(dataSource);

    }



    @Test
    void shouldListInsertedProjectMembers () throws SQLException {
        ProjectMember projectMember = exampleProject();
        projectMemberDao.insert(projectMember);
        assertThat(projectMemberDao.list())
                .extracting(ProjectMember::getFirstName)
                .contains(projectMember.getFirstName());
    }

    @Test
    void shouldReturnProjectMemberAsOptions() throws SQLException {
        ProjectMemberOptionsController controller = new ProjectMemberOptionsController(projectMemberDao);
        ProjectMember projectMember = ProjectMemberDaoTest.exampleProject();
        projectMemberDao.insert(projectMember);

        assertThat(controller.getBody())
                .contains("<option value=" + projectMember.getId() + ">" + projectMember.getFirstName() + "</option>");
    }
    
    public static ProjectMember exampleProject() {
        ProjectMember projectMember = new ProjectMember();
        projectMember.setFirstName(exampleProjectFirstName());
        projectMember.setTaskId(1);
        return projectMember;
    }


    private static String exampleProjectFirstName() {
        String[] options = {"Andreas", "Fredrik", "Pernille"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }

}


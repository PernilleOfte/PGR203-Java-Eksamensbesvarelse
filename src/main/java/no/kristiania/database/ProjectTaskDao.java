package no.kristiania.database;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectTaskDao extends AbstractDao<ProjectTask> {

    public ProjectTaskDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected ProjectTask mapRow(ResultSet rs) throws SQLException {
        ProjectTask task = new ProjectTask();
        task.setId(rs.getInt("id"));
        task.setName(rs.getString("name"));
        task.setStatus(rs.getString("status"));
        return task;
    }

    public List<ProjectTask> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM project_task")) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<ProjectTask> project = new ArrayList<>();
                    while (rs.next()) {
                        project.add(mapRow(rs));
                    }
                    return project;
                }
            }
        }
    }

    public void insert(ProjectTask task) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO project_task (name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, task.getName());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();


                    task.setId(generatedKeys.getInt("id"));
                }
            }
        }
    }

    public void update(ProjectTask projectTask) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE project_task SET status = ? WHERE id = ?"
            )) {
                statement.setString(1, projectTask.getStatus());
                statement.setInt(2, projectTask.getId());
                statement.executeUpdate();
            }
        }
    }

    public ProjectTask retrieve(Integer id) throws SQLException {
        return retrieve(id, "SELECT * FROM project_task WHERE id = ?");
    }
}





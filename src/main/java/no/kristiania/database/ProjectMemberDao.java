package no.kristiania.database;


import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ProjectMemberDao extends AbstractDao<ProjectMember> {

    public ProjectMemberDao(DataSource dataSource) {
        super(dataSource);
    }


    // Insert First Name
    public void insert(ProjectMember projectMember) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO project_Member (first_name, last_name, email) VALUES (?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, projectMember.getFirstName());
                statement.setString(2, projectMember.getLastName());
                statement.setString(3, projectMember.getEmail());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    projectMember.setId(generatedKeys.getInt("id"));
                }
            }
        }
    }

    public void update(ProjectMember projectMember) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE project_member SET project_task_id = ? WHERE id = ?"
            )) {
                statement.setInt(1, projectMember.getTaskId());
                statement.setInt(2, projectMember.getId());
                statement.executeUpdate();
            }
        }
    }

    public ProjectMember retrieve(Integer id) throws SQLException {
        return retrieve(id, "SELECT * FROM project_Member WHERE id = ?");
    }

    public List<ProjectMember> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM project_member")) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<ProjectMember> projectMember = new ArrayList<>();
                    while (rs.next()) {
                        projectMember.add(mapRow(rs));
                    }
                    return projectMember;
                }
            }
        }
    }

    public List<ProjectMember> listByTaskId(int taskId) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select member.*, project.status from project_member member," +
                    "project_task project where member.project_task_id =  ? and project.id = member.project_task_id")) {
                statement.setInt(1, taskId);
                try (ResultSet rs = statement.executeQuery()) {
                    List<ProjectMember> projectMembers = new ArrayList<>();
                    while (rs.next()) {
                        ProjectMember projectMember = mapRow(rs);
                        projectMember.setCurrentStatus(rs.getString("status"));
                        projectMembers.add(projectMember);
                    }
                    return projectMembers;
                }
            }
        }
    }

    @Override
    protected ProjectMember mapRow(ResultSet rs) throws SQLException {
        ProjectMember projectMember = new ProjectMember();
        projectMember.setId(rs.getInt("id"));
        projectMember.setFirstName(rs.getString("first_name"));
        projectMember.setLastName(rs.getString("last_name"));
        projectMember.setEmail(rs.getString("email"));
        projectMember.setTaskId((Integer) rs.getObject("id"));

        return projectMember;
    }
}


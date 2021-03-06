package com.example.nyurates.dao.DaoImpl;

import com.example.nyurates.dao.PublicDao;
import com.example.nyurates.entity.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class PublicDaoImpl implements PublicDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    NamedParameterJdbcTemplate namedJdbcTemplate;

    /**
     * Student Login
     * @param student
     * @return Student
     */
    @Override
    public Student studentLogin(Student student) {
        String query = "SELECT netid, name FROM Student WHERE email = ? AND password= ?";
        try{
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, student.getEmail(), DigestUtils.md5Hex(student.getPassword()));
            if (result.size() == 1) {
                Map<String, Object> map = result.get(0);
                student.setName((String) map.get("name"));
                student.setNetid((String) map.get("netid"));
                return student;
            }
        } catch (DataAccessException e){
            SQLException exception = (SQLException) e.getCause();
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Professor Login
     * @param professor
     * @return Professor
     */
    @Override
    public Professor professorLogin(Professor professor){
        String query = "SELECT netid, name FROM Professor WHERE email = ? AND password= ?";
        try{
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, professor.getEmail(), DigestUtils.md5Hex(professor.getPassword()));
            if (result.size() == 1) {
                Map<String, Object> map = result.get(0);
                professor.setName((String) map.get("name"));
                professor.setNetid((String) map.get("netid"));
                return professor;
            }
        } catch (DataAccessException e){
            SQLException exception = (SQLException) e.getCause();
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Student Register
     * @param student
     * @return boolean
     */
    @Override
    public boolean studentRegist(Student student) {
        String query = "INSERT INTO Student VALUES (?, ?, ?, ?)";
        try{
            jdbcTemplate.update(query, student.getEmail(), student.getNetid(), student.getName(), DigestUtils.md5Hex(student.getPassword()));
            return true;
        } catch (DataAccessException e) {
            SQLException exception = (SQLException) e.getCause();
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * Professor Register
     * @param professor
     * @param is_member
     * @return boolean
     */
    @Override
    public boolean professorRegist(Professor professor, boolean is_member){
        String query;
        if (is_member){
            query = "UPDATE Professor SET is_member = 1, visible = 1, password = ? WHERE email = ?";
            try{
                jdbcTemplate.update(query, DigestUtils.md5Hex(professor.getPassword()), professor.getEmail());
                return true;
            } catch (DataAccessException e) {
                SQLException exception = (SQLException) e.getCause();
                exception.printStackTrace();
            }
        }
        else{
            query = "INSERT INTO Professor VALUES (?, ?, ?, ?, ?, 1, 1)";
            try{
                jdbcTemplate.update(query, professor.getEmail(), professor.getNetid(), professor.getName(), DigestUtils.md5Hex(professor.getPassword()), professor.getDept());
                return true;
            } catch (DataAccessException e) {
                SQLException exception = (SQLException) e.getCause();
                exception.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Search By Email
     * @param student
     * @return Student
     */
    @Override
    public Student searchByEmail(Student student){
        String query = "SELECT netid, name FROM Student WHERE email = ?";
        try{
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, student.getEmail());
            if (result.size() == 1) {
                Map<String, Object> map = result.get(0);
                student.setName((String) map.get("name"));
                student.setNetid((String) map.get("netid"));
                return student;
            }
        } catch (DataAccessException e){
            SQLException exception = (SQLException) e.getCause();
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Match Course
     * @param course
     * @return Course
     */
    @Override
    public Course matchCourse(Course course){
        String queryCourseName = "SELECT course_name FROM Course WHERE code = ?";
        String queryCourseCode = "SELECT code FROM Course WHERE course_name = ?";
        try{
            if(course.getCourse_code() != null){
                List<Map<String, Object>> result = jdbcTemplate.queryForList(queryCourseName, course.getCourse_code());
                if (result.size() >0) {
                    Map<String, Object> map = result.get(0);
                    course.setCourse_name((String) map.get("course_name"));
                    return course;
                }
            }else{
                List<Map<String, Object>> result = jdbcTemplate.queryForList(queryCourseCode, course.getCourse_name());
                if (result.size() >0) {
                    Map<String, Object> map = result.get(0);
                    course.setCourse_code((String) map.get("code"));
                    return course;
                }
            }
        } catch (DataAccessException e){
            SQLException exception = (SQLException) e.getCause();
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Get Offered Semester
     * @param course
     * @return ArrayList<String>
     */
    @Override
    public ArrayList<String> getOfferedSemester(Course course){
        String query = "SELECT semester FROM Course WHERE code = ?";
        ArrayList<String> result = new ArrayList<String>();
        try{
            if(course.getCourse_code() != null){
                List<Map<String, Object>> queryResult = jdbcTemplate.queryForList(query, course.getCourse_code());
                for (int i=0; i<queryResult.size(); i++){
                    Map<String, Object> map = queryResult.get(i);
                    String semester = (String) map.get("semester");
                    result.add(semester);
                }
                System.out.println(result);
                return result;
            }
        }
        catch (DataAccessException e){
            return result;
        }
        return result;
    }

    /**
     * Search Course
     * @param course
     * @return ArrayList<Course>
     */
    @Override
    public ArrayList<Course> searchCourse(Course course){
        String query = "SELECT * FROM SearchCourse WHERE (code LIKE ? OR course_name LIKE ?)";
        ArrayList<Course> resultList = new ArrayList<Course>();
        try{
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, "%"+course.getCourse_code()+"%", "%"+course.getCourse_name()+"%");
            if (result.size() > 0) {
                for (int i=0; i<result.size(); i++){
                    Course result_course = new Course();
                    Map<String, Object> map = result.get(i);
                    result_course.setCourse_name((String) map.get("course_name"));
                    result_course.setCourse_code((String) map.get("code"));
                    result_course.setDept_name((String) map.get("department"));
                    try{
                        result_course.setHot_comment((String) map.get("hot_comment"));
                        result_course.setRate((double) map.get("rate"));
                    }
                    catch (NullPointerException e){
                        result_course.setHot_comment("No comments have been made to this course yet.");
                        result_course.setRate(0);
                    }
                    resultList.add(result_course);
                }
                return resultList;
            }
        } catch (Exception e){
            SQLException exception = (SQLException) e.getCause();
            exception.printStackTrace();
        }
        return resultList;
    }

    /**
     * Search Course Comments
     * @param course
     * @return ArrayList<Comment>
     */
    @Override
    public ArrayList<Comment> searchComments(Course course){
        String query = "SELECT * FROM Comments WHERE course_code = ?";
        ArrayList<Comment> comments = new ArrayList<Comment>();
        try{
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, course.getCourse_code());
            if (result.size() > 0) {
                for (int i = 0; i < result.size(); i ++){
                    Comment comment = new Comment();
                    Map<String, Object> map = result.get(i);
                    comment.setComment_id((Long) map.get("comment_id"));
                    comment.setContent((String) map.get("content"));
                    comment.setDate((LocalDateTime) map.get("time"));
                    comment.setLikes((Long) map.get("likes"));
                    comment.setDislikes((Long) map.get("dislikes"));
                    comment.setRate((Double) map.get("rate"));
                    comment.setCourse_code((String) map.get("course_code"));
                    comment.setSemester((String) map.get("semester"));
                    comment.setProfessor_id((String) map.get("professor_id"));
                    comment.setStudent_id((String) map.get("user_id"));
                    comments.add(comment);
                }
            }
        } catch (DataAccessException e){
            SQLException exception = (SQLException) e.getCause();
            exception.printStackTrace();
        }
        return comments;
    }

    /**
     * Search Professor Comments
     * @param professor
     * @return ArrayList<Comment>
     */
    @Override
    public ArrayList<Comment> searchComments(Professor professor){
        String query = "SELECT DISTINCT * FROM Comments " +
                "INNER JOIN Professor P on Comments.professor_id = P.netid " +
                "INNER JOIN Course C on Comments.course_code = C.code " +
                "WHERE Comments.professor_id = ? AND Comments.semester = C.semester; ";
        ArrayList<Comment> comments = new ArrayList<Comment>();
        try{
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, professor.getNetid());
            if (result.size() > 0) {
                for (int i = 0; i < result.size(); i ++){
                    Comment comment = new Comment();
                    Map<String, Object> map = result.get(i);
                    comment.setComment_id((Long) map.get("comment_id"));
                    comment.setContent((String) map.get("content"));
                    comment.setDate((LocalDateTime) map.get("time"));
                    comment.setLikes((Long) map.get("likes"));
                    comment.setDislikes((Long) map.get("dislikes"));
                    comment.setRate((Double) map.get("rate"));
                    comment.setCourse_code((String) map.get("course_code"));
                    comment.setSemester((String) map.get("semester"));
                    comment.setProfessor_id((String) map.get("professor_id"));
                    comment.setStudent_id((String) map.get("user_id"));
                    comment.setProfessor_name((String) map.get("name"));
                    comment.setCourse_name((String) map.get("course_name"));
                    comments.add(comment);
                }
            }
        } catch (DataAccessException e){
            SQLException exception = (SQLException) e.getCause();
            exception.printStackTrace();
        }
        return comments;
    }

    /**
     * Search Student Comments
     * @param student
     * @return ArrayList<Comment>
     */
    @Override
    public ArrayList<Comment> searchComments(Student student){
        String query = "SELECT * FROM Comments " +
                "INNER JOIN Professor P on Comments.professor_id = P.netid " +
                "INNER JOIN Course C on Comments.course_code = C.code " +
                "WHERE Comments.user_id = ? AND Comments.semester = C.semester;";
        ArrayList<Comment> comments = new ArrayList<>();
        try{
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, student.getNetid());
            System.out.println(result);
            if (result.size() > 0) {
                for (int i = 0; i < result.size(); i ++){
                    Comment comment = new Comment();
                    Map<String, Object> map = result.get(i);
                    comment.setComment_id((Long) map.get("comment_id"));
                    comment.setContent((String) map.get("content"));
                    comment.setDate((LocalDateTime) map.get("time"));
                    comment.setLikes((Long) map.get("likes"));
                    comment.setDislikes((Long) map.get("dislikes"));
                    comment.setRate((Double) map.get("rate"));
                    comment.setCourse_code((String) map.get("course_code"));
                    comment.setSemester((String) map.get("semester"));
                    comment.setProfessor_id((String) map.get("professor_id"));
                    comment.setStudent_id((String) map.get("user_id"));
                    comment.setProfessor_name((String) map.get("name"));
                    comment.setCourse_name((String) map.get("course_name"));
                    comments.add(comment);
                }
            }
        } catch (DataAccessException e){
            SQLException exception = (SQLException) e.getCause();
            exception.printStackTrace();
        }
        return comments;
    }

    /**
     * Search Course Average Rating
     * @param course
     * @return rating
     */
    @Override
    public double searchAverageRating(Course course){
        String query = "SELECT ROUND(AVG(rate), 2) AS AverageRate FROM Comments WHERE course_code = ?";
        double rating = 0;
        try{
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, course.getCourse_code());
            if (result.size() == 1) {
                Map<String, Object> map = result.get(0);
                rating = (Double) map.get("AverageRate");
                return rating;
            }
        } catch (Exception e){
            return rating;
        }
        return rating;
    }

    /**
     * Search Professor Average Rating
     * @param professor
     * @return rating
     */
    @Override
    public double searchAverageRating(Professor professor){
        String query = "SELECT code FROM Course WHERE professor_id = ?";
        double rating = 0;
        try{
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, professor.getNetid());
            double total_rating = 0;
            if (result.size() > 0) {
                for (int i = 0; i < result.size(); i ++){
                    Course course = new Course();
                    Map<String, Object> map = result.get(i);
                    course.setCourse_code((String) map.get("code"));
                    total_rating = total_rating + searchAverageRating(course);
                }
                rating = total_rating / result.size();
                return rating;
            }
        } catch (DataAccessException e){
            SQLException exception = (SQLException) e.getCause();
            exception.printStackTrace();
        }
        return rating;
    }

    /**
     * Match Professor
     * @param professor
     * @return Professor
     */
    @Override
    public Professor matchProfessor(Professor professor){
        String queryProfessorName = "SELECT name, department FROM Professor WHERE netid = ? AND visible = 1";
        String queryProfessorID = "SELECT netid, department FROM Professor WHERE name = ? AND visible = 1";
        try{
            if(professor.getNetid() != null){
                List<Map<String, Object>> result = jdbcTemplate.queryForList(queryProfessorName, professor.getNetid());
                if (result.size() > 0) {
                    Map<String, Object> map = result.get(0);
                    professor.setName((String) map.get("name"));
                    professor.setDept((String) map.get("department"));
                    return professor;
                }
            }else{
                List<Map<String, Object>> result = jdbcTemplate.queryForList(queryProfessorID, professor.getName());
                if (result.size() >0) {
                    Map<String, Object> map = result.get(0);
                    professor.setNetid((String) map.get("netid"));
                    professor.setDept((String) map.get("department"));
                    return professor;
                }
            }
        } catch (DataAccessException e){
            SQLException exception = (SQLException) e.getCause();
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Search Professor
     * @param professor
     * @return ArrayList<Professor>
     */
    @Override
    public ArrayList<Professor> searchProfessor(Professor professor){
        String query= "SELECT name, netid, department, " +
        "IFNULL((SELECT content FROM Comments WHERE Comments.professor_id = Professor.netid ORDER BY likes DESC LIMIT 1), '') AS hot_comment, " + 
        "IFNULL((SELECT ROUND(AVG(rate), 2) FROM Comments WHERE professor_id = Professor.netid), 0) AS rate " + 
        "FROM Professor WHERE (netid LIKE ? OR name LIKE ?) AND visible = 1";
        ArrayList<Professor> resultList = new ArrayList<Professor>();
        try{
            System.out.println("x");
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, "%"+professor.getNetid()+"%", "%"+professor.getName()+"%");
            if (result.size() > 0) {
                for (int i=0; i<result.size(); i++){
                    Professor resultProf = new Professor();
                    Map<String, Object> map = result.get(i);
                    resultProf.setName((String) map.get("name"));
                    resultProf.setNetid((String) map.get("netid"));
                    resultProf.setDept((String) map.get("department"));
                    resultProf.setHot_comment((String) map.get("hot_comment"));
                    resultProf.setRate((double) map.get("rate"));
                    resultList.add(resultProf);
                }
                return resultList;
            }
        } catch (Exception e){
            Exception exception = (Exception) e.getCause();
            exception.printStackTrace();
        }
        return resultList;
    }

    /**
     * Search Professor Course
     * @param professor
     * @return ArrayList<Course>
     */
    @Override
    public ArrayList<Course> searchProfessorCourse(Professor professor){
        String query = "SELECT * FROM Course WHERE professor_id = ?";
        ArrayList<Course> courses = new ArrayList<Course>();
        try{
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, professor.getNetid());
            if (result.size() > 0) {
                for (int i = 0; i < result.size(); i ++){
                    Course course = new Course();
                    Map<String, Object> map = result.get(i);
                    course.setCourse_name((String) map.get("course_name"));
                    course.setCourse_code((String) map.get("code"));
                    course.setSemester((String) map.get("semester"));
                    course.setLocation((String) map.get("location"));
                    course.setDept_name((String) map.get("department"));
                    course.setProfessor_id(professor.getNetid());
                    courses.add(course);
                }
            }
        } catch (DataAccessException e){
            SQLException exception = (SQLException) e.getCause();
            exception.printStackTrace();
        }
        return courses;
    }

    /**
     * Search Professor By Course
     * @param code
     * @param semester
     * @return String
     */
    @Override
    public String getProfByCourse(String code, String semester){
        String query = "SELECT professor_id FROM Course WHERE code = ? AND semester = ?";
        try{
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, code, semester);
            return (String) result.get(0).get("professor_id");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Post Comments
     * @param comment
     * @return boolean
     */
    @Override
    public boolean postComment(Comment comment){
        String query = "INSERT INTO Comments(content, time, likes, dislikes, rate, course_code, semester, professor_id, user_id) VALUES (?, NOW(), 0, 0, ?, ?, ?, ?, ?)";
        try{
            String professor_id = this.getProfByCourse(comment.getCourse_code(), comment.getSemester());
            jdbcTemplate.update(query, comment.getContent(), comment.getRate(), comment.getCourse_code(), comment.getSemester(), professor_id, comment.getStudent_id());
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    /**
     * Handle likes and dislikes
     * @param comment_id
     * @param isLike
     * @return boolean
     */
    @Override
    public boolean handleLike(Long comment_id, boolean isLike){
        String query = "SELECT likes, dislikes FROM Comments WHERE comment_id = ?";
        try{
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, comment_id);
            if (result.size() == 1) {
                Map<String, Object> map = result.get(0);
                Long likes = (Long) map.get("likes");
                Long dislikes = (Long) map.get("dislikes");
                if (isLike){
                    likes += 1;
                    String queryLike = "UPDATE Comments SET likes = ? WHERE comment_id = ? ";
                    jdbcTemplate.update(queryLike, likes, comment_id);
                    return true;
                }else{
                    dislikes += 1;
                    String queryDislike = "UPDATE Comments SET dislikes = ? WHERE comment_id = ? ";
                    jdbcTemplate.update(queryDislike, dislikes, comment_id);
                    return true;
                }
            }
        } catch (DataAccessException e) {
            SQLException exception = (SQLException) e.getCause();
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * Add Professor Request
     * @param prof_req
     * @return String
     */
    @Override
    public boolean addprofessor(Prof_req prof_req){
        String query = "INSERT INTO Prof_req(professor_name, professor_department, professor_email, professor_course_name, professor_course_code, professor_course_semester) VALUES (?, ?, ?, ?, ?, ?)";
        try{
            jdbcTemplate.update(query, prof_req.getProfessor_name(), prof_req.getProfessor_dept(), prof_req.getProfessor_email(), prof_req.getProfessor_course_name(), prof_req.getProfessor_course_code(), prof_req.getProfessor_course_semester());
            return true;
        } catch (DataAccessException e) {
            SQLException exception = (SQLException) e.getCause();
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * Get User By Comment
     * @param comment_id
     * @return String
     */
    @Override
    public String getUserByComment(Long comment_id) {
        String query = "SELECT user_id FROM Comments WHERE comment_id = ?";
        try{
            List<Map<String, Object>> result = jdbcTemplate.queryForList(query, comment_id);
            if(result.size() > 0){
                return (String) result.get(0).get("user_id");
            }
        } catch (DataAccessException e) {
            SQLException exception = (SQLException) e.getCause();
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * Report Comments
     * @param report
     * @return boolean
     */
    @Override
    public boolean reportComment(Report report){
        String user_id = this.getUserByComment(report.getComment_id());
        if (user_id != null){
            String query = "INSERT INTO Report(comment_id, comment_user, report_date, report_reason, status) VALUES (?, ?, NOW(), ?, ?)";
            try{
                jdbcTemplate.update(query, report.getComment_id(), user_id, report.getReport_reason(), "Processing");
                return true;
            } catch (DataAccessException e) {
                SQLException exception = (SQLException) e.getCause();
                exception.printStackTrace();
            }
            return false; 
        }
        else{
            return false;
        }
        
    }

}

package com.example.nyurates.service;

import com.example.nyurates.dao.PublicDao;
import com.example.nyurates.entity.Comment;
import com.example.nyurates.entity.Course;
import com.example.nyurates.entity.Professor;
import com.example.nyurates.entity.Student;
import com.example.nyurates.entity.results.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional(rollbackFor = RuntimeException.class)
public class PublicServiceImpl implements PublicService {

    //植入dao层对象
    @Autowired
    private PublicDao dao;

    /**
     * Register Student
     * @param student
     * @return Result
     */
    public Result regist_student(Student student) {
        Result result = new Result();
        result.setCode(400);
        try {
            Student existStudent = dao.searchByEmail(student);
            if(existStudent != null){
                // student already existed
                result.setMsg("The account has existed. Failed to register.");
                result.setCode(400);
            }else{
                boolean r = dao.studentRegist(student);
                if (r){
                    System.out.println(student.getNetid());
                    result.setMsg("Successfully registered!");
                    result.setCode(200);
                }
            }
        } catch (Exception e) {
            result.setMsg(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Reguster Professor
     * @param professor
     * @return Result
     */
    public Result regist_prof(Professor professor){
        Result result = new Result();
        result.setCode(400);
        try {
            Professor existProfessor = dao.matchProfessor(professor);
            if(existProfessor == null){
                // professor does not exist
                boolean r = dao.professorRegist(professor, false);
                if (r){
                    result.setMsg("Successfully registered!");
                    result.setCode(200);
                }
                else{
                    result.setMsg("Register failure.");
                    result.setCode(400);
                }
            }
            else{
                if(existProfessor.getIs_member() == 0){
                    boolean r = dao.professorRegist(professor, true);
                    if (r){
                        result.setMsg("Successfully registered!");
                        result.setCode(200);
                    }
                    else{
                        result.setMsg("Register failure.");
                        result.setCode(400);
                    }
                }
            }
        } catch (Exception e) {
            result.setMsg(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Login
     * @param student
     * @return LoginResult
     */
    public LoginResult loginStudent(Student student) {
        LoginResult loginResult = new LoginResult();
        loginResult.setCode(400);
        try {
            Student std= dao.studentLogin(student);
            if(std == null){
                loginResult.setMsg("Unable to login with provided credentials.");
                loginResult.setCode(400);
            }else{
                loginResult.setMsg("Successfully Logged in!");
                loginResult.setCode(200);
                loginResult.setUsername(std.getName());
            }
        } catch (Exception e) {
            loginResult.setMsg(e.getMessage());
            e.printStackTrace();
        }
        return loginResult;
    }

    /**
     * Login Professor
     * @param professor
     * @return LoginResult
     */
    public LoginResult loginProfessor(Professor professor){
        LoginResult loginResult = new LoginResult();
        loginResult.setCode(400);
        try {
            Professor pro= dao.professorLogin(professor);
            if(pro == null){
                loginResult.setMsg("Unable to login with provided credentials.");
                loginResult.setCode(400);
            }else{
                loginResult.setMsg("Successfully Logged in!");
                loginResult.setCode(200);
                loginResult.setUsername(pro.getName());
            }
        } catch (Exception e) {
            loginResult.setMsg(e.getMessage());
            e.printStackTrace();
        }
        return loginResult;
    }

    /**
     * View Course
     * @param course
     * @return ViewCourseResult
     */
    public ViewCourseResult view_course(Course course){
        ViewCourseResult viewCourseResult = new ViewCourseResult();
        viewCourseResult.setCode(400);

        try{
            course = dao.matchCourse(course);
            if(course == null){
                viewCourseResult.setMsg("Unable to query the provided course code.");
                viewCourseResult.setCode(400);
            }else{
                viewCourseResult.setMsg("Successfully get course!");
                viewCourseResult.setCode(200);
                viewCourseResult.setCourse_code(course.getCourse_code());
                viewCourseResult.setCourse_name(course.getCourse_name());
                ArrayList<Comment> comments = dao.searchComments(course);
                viewCourseResult.setComments(comments);
                double rating = dao.searchAverageRating(course);
                viewCourseResult.setRating(rating);
                ArrayList<String> offered = dao.getOfferedSemester(course);
                viewCourseResult.setOffered_in(offered);
                viewCourseResult.setComments_num(comments.size());
            }
        } catch (Exception e) {
            viewCourseResult.setMsg(e.getMessage());
            e.printStackTrace();
        }

        return viewCourseResult;
    }

    /**
     * View Professor
     * @param professor
     * @return ViewProfessorRrsult
     */
    public ViewProfessorResult view_professor(Professor professor){
        ViewProfessorResult viewProfessorResult = new ViewProfessorResult();
        viewProfessorResult.setCode(400);

        try{
            professor = dao.matchProfessor(professor);
            if(professor == null){
                viewProfessorResult.setMsg("Unable to query the professor.");
                viewProfessorResult.setCode(400);
            }else{
                viewProfessorResult.setMsg("Successfully get professor!");
                viewProfessorResult.setCode(200);
                viewProfessorResult.setProfessor_name(professor.getName());
                viewProfessorResult.setDepartment(professor.getDept());

                double rating = dao.searchAverageRating(professor);
                viewProfessorResult.setRating(rating);

                ArrayList<Comment> comments = dao.searchComments(professor);
                viewProfessorResult.setComments(comments);
                viewProfessorResult.setTotal_comments(comments.size());

                ArrayList<Course> courses = dao.searchProfessorCourse(professor);
                viewProfessorResult.setCourses(courses);
            }
        } catch (Exception e) {
            viewProfessorResult.setMsg(e.getMessage());
            e.printStackTrace();
        }

        return viewProfessorResult;
    }

    /**
     * Load Comments
     * @param course
     * @return CommentsResult
     */
    public CommentsResult load_comments(Course course){
        CommentsResult commentsResult = new CommentsResult();
        commentsResult.setCode(400);

        try{
            ArrayList<Comment> comments = dao.searchComments(course);
            if(comments.size() > 0){
                commentsResult.setMsg("Successfully get comments");
                commentsResult.setCode(200);
                commentsResult.setComments(comments);
            }else{
                commentsResult.setMsg("Unable to query comments");
            }
        } catch (Exception e) {
            commentsResult.setMsg(e.getMessage());
            e.printStackTrace();
        }
        return commentsResult;
    }

    /**
     * Search course
     * @param course
     * @return CourseListResult
     */
    public CourseListResult search_course(Course course){
        CourseListResult courseListResult = new CourseListResult();
        courseListResult.setCode(400);

        try{
            ArrayList<Course> daoResult = dao.searchCourse(course);
            courseListResult.setCourse_list(daoResult);
            courseListResult.setCode(200);
            courseListResult.setMsg("Suceess");
        } catch (Exception e) {
            courseListResult.setMsg(e.getMessage());
            e.printStackTrace();
        }

        return courseListResult;
    }

    /**
     * Search Professor
     * @param professor
     * @return ProfListResult
     */
    public ProfListResult search_professor(Professor professor){
        ProfListResult result = new ProfListResult();
        result.setCode(400);
        try{
            ArrayList<Professor> daoResult = dao.searchProfessor(professor);
            result.setProfList(daoResult);
            result.setCode(200);
            result.setMsg("Success");
        }
        catch (Exception e){
            return result;
        }
        return result;
    }

}

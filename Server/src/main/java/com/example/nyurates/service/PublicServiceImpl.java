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
     * 注册
     * @param student 参数封装
     * @return Result
     */
    public LoginResult regist(Student student) {
        LoginResult loginResult = new LoginResult();
        loginResult.setCode(400);
        try {
            Student existStudent = dao.searchByEmail(student);
            if(existStudent != null){
                // student already existed
                loginResult.setMsg("The account has existed. Failed to register.");
                loginResult.setCode(400);
            }else{
                boolean r = dao.studentRegist(student);
                if (r){
                    System.out.println(student.getNetid());
                    loginResult.setMsg("Successfully registered!");
                    loginResult.setCode(200);
                }
            }
        } catch (Exception e) {
            loginResult.setMsg(e.getMessage());
            e.printStackTrace();
        }
        return loginResult;
    }

    /**
     * Login
     * @param student 用户名和密码
     * @return LoginResult
     */
    public LoginResult login(Student student) {
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

    public ViewCourseResult view_course(Course course){
        ViewCourseResult viewCourseResult = new ViewCourseResult();
        viewCourseResult.setCode(400);

        try{
            course = dao.searchCourse(course);
            if(course == null){
                viewCourseResult.setMsg("Unable to query the provided course code.");
                viewCourseResult.setCode(400);
            }else{
                viewCourseResult.setMsg("Successfully get course!");
                viewCourseResult.setCode(200);
                viewCourseResult.setCourse_code(course.getCourse_code());
                viewCourseResult.setCourse_name(course.getCourse_name());
                ArrayList<Comment> comments = dao.searchCourseComments(course);
                viewCourseResult.setComments(comments);
                double rating = dao.searchAverageRating(course);
                viewCourseResult.setRating(rating);
                viewCourseResult.setComments_num(comments.size());
            }
        } catch (Exception e) {
            viewCourseResult.setMsg(e.getMessage());
            e.printStackTrace();
        }

        return viewCourseResult;
    }

    public CommentsResult load_comments(Course course){
        CommentsResult commentsResult = new CommentsResult();
        commentsResult.setCode(400);

        try{
            ArrayList<Comment> comments = dao.searchCourseComments(course);
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

    public SearchCourseResult search_course(Course course){
        SearchCourseResult searchCourseResult = new SearchCourseResult();
        searchCourseResult.setCode(400);

        try{
            course = dao.searchCourse(course);
            if(course == null){
                searchCourseResult.setMsg("Unable to query comments");
            }else{
                searchCourseResult.setMsg("Successfully searched course");
                searchCourseResult.setCode(200);
                searchCourseResult.setCourse_name(course.getCourse_name());
                searchCourseResult.setCourse_code(course.getCourse_code());
                double rating = dao.searchAverageRating(course);
                searchCourseResult.setRating(rating);
            }
        } catch (Exception e) {
            searchCourseResult.setMsg(e.getMessage());
            e.printStackTrace();
        }

        return searchCourseResult;
    }

    public SearchProfessorResult search_professor(Professor professor){
        SearchProfessorResult searchProfessorResult = new SearchProfessorResult();
        searchProfessorResult.setCode(400);

        try{
            professor = dao.searchProfessor(professor);
            if(professor == null){
                searchProfessorResult.setMsg("Unable to query professor");
            }else{
                searchProfessorResult.setMsg("Successfully searched course");
                searchProfessorResult.setCode(200);
                searchProfessorResult.setProfessor_name(professor.getName());
                searchProfessorResult.setProfessor_id(professor.getNetid());
                searchProfessorResult.setVisible(professor.getVisible());
                double rating = dao.searchAverageRating(professor);
                searchProfessorResult.setRating(rating);
            }
        } catch (Exception e) {
            searchProfessorResult.setMsg(e.getMessage());
            e.printStackTrace();
        }

        return searchProfessorResult;
    }

}
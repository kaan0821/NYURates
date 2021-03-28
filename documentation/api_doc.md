This is the API documentation of this project. Largely divided into four parts: 
* Public APIs
* Student APIs
* Professor APIs
* Administrator APIs
# Public APIs 
## View Course Page

Used to view rating of a specified course, within a specific timespan.

**URL** : `/api/public/view_course/`

**Method** : `GET`

**Auth required** : NO

**Data constraints**

```json
{
    "course_id": "[valid course_id]",
    "timespan": [
        "[start_semester]",
        "[end_semester]"
    ]
}
```


### Success Response

**Code** : `200 OK`

**Content example**

```json
{
    "course_name": "[corresponding course name]",
    "rating": (Float) average rating of the input course,
    "total_comments": (Int) total number of comments,
    "comments": [
        {
            "time": "[yyyy-mm-dd hh:mm]",
            "username": "[username of comment]",
            "rating": "[rating of this comment]",
            "content": "[comment body]"
        }
    ],
    "offered_semesters": [
        "[semesters that this course is offered]"
    ]
}
```

### Error Response

**Condition** : If wrong course code is provided or course not in database.

**Code** : `400 BAD REQUEST`

**Content** :

```json
{
    "err_msg": [
        "Unable to query the provided course code."
    ]
}
```

## View Professor Pages

Used to view rating of a specified professor, within a specific timespan.

**URL** : `/api/public/view_professor/`

**Method** : `GET`

**Auth required** : NO

**Data constraints**

```json
{
    "course_id": "[valid professor_netid]",
    "timespan": [
        "[start_semester]",
        "[end_semester]"
    ]
}
```


### Success Response

**Code** : `200 OK`

**Content example**

```json
{
    "professor_name": "[corresponding professor name]",
    "department": "[department of the professor]",
    "rating": (Float) average rating of the input professor,
    "total_comments": (Int) total number of comments,
    "comments": [
        {
            "time": "[yyyy-mm-dd hh:mm]",
            "username": "[username of comment]",
            "course_code": "[valid course code]",
            "rating": (Float) rating of this comment, 
            "content": "[comment body]"
        }
    ],
    "course_history": [
        {
            "course_name": "[valid course name]",
            "course_code": "[valid course code]",
            "semester": "[corresponing semester]",
            "rating": (Float) avg rating in that semester
        }
    ]
}
```

### Error Response

**Condition** : If wrong professor netid is provided or professor not in database.

**Code** : `400 BAD REQUEST`

**Content** :

```json
{
    "err_msg": [
        "Unable to query the provided professor netid."
    ]
}
```

## Load Comments
Used to dynamically load comments, if too many of them.

**URL** : `/api/public/load_comments/`

**Method** : `GET`

**Auth required** : NO

**Data constraints**
```json
{
    "search_type": "course" or "professor",
    "course_code": "[valid course code]",
    "professor_id": "[valid professor netid]",
    "start_idx": (Int) Starting index of the query.
}
```

### Success Response

**Code** : `200 OK`

**Content example**

```json
{
    "comments": [
        {
            "time": "[yyyy-mm-dd hh:mm]",
            "username": "[username of comment]",
            "course_code": "[valid course code]",
            "rating": (Float) rating of this comment, 
            "content": "[comment body]"
        }
    ]
}
```

### Error Response

**Condition** : Wrong input or professor not in database.

**Code** : `400 BAD REQUEST`

**Content** :

```json
{
    "err_msg": [
        "Unable to query the input professor."
    ]
}
```

## Register

Used to register a new account in the application.

**URL** : `/api/public/register/`

**Method** : `POST`

**Auth required** : NO

**Data constraints**

```json
{
    "username": "[valid email address]",
    "role": "[student or professor]",
    "password": "[password in plain text]"
}
```

### Success Response

**Code** : `200 OK`

### Error Response

**Condition** : If 'username' has existed. 

**Code** : `400 BAD REQUEST`

**Content** :

```json
{
    "err_msg": [
        "The account has existed. Falied to resgister."
    ]
}
```

## Login

Used to authenticate for a registered Student/Professor.

**URL** : `/api/public/login/`

**Method** : `POST`

**Auth required** : NO

**Data constraints**

```json
{
    "username": "[valid email address]",
    "role": "[student or professor]",
    "password": "[password in plain text]"
}
```


### Success Response

**Code** : `200 OK`

**Content example**

```json
{
    "token": "[valid user token]"
}
```

### Error Response

**Condition** : If 'username' and 'password' combination is wrong or illegal role specified.

**Code** : `400 BAD REQUEST`

**Content** :

```json
{
    "err_msg": [
        "Unable to login with provided credentials."
    ]
}
```

## Search Course

Used to search for a course.

**URL** : `/api/public/search_course/`

**Method** : `POST`

**Auth required** : NO

**Data constraints**

*Specify search type, and provide either field will do the job.

```json
{
    "search_type": "[name or code]",
    "course_name": "[valid course name]",
    "course_code": "[valid course code]"
}
```


### Success Response

**Code** : `200 OK`

**Content example**

```json
{
    "course_link": "[inputed course link]",
    "rating": (Float) rating of the course
}
```

### Error Response

**Condition** : Wrong input or course not in database.

**Code** : `400 BAD REQUEST`

**Content** :

```json
{
    "err_msg": [
        "Unable to query the input course."
    ]
}
```

## Search Professor

Used to search for a professor.

**URL** : `/api/public/search_professor/`

**Method** : `POST`

**Auth required** : NO

**Data constraints**

*Specify search type, and provide either field will do the job.

```json
{
    "search_type": "name" or "id",
    "professor_name": "[valid professor name]",
    "professor_id": "[valid professor id]"
}
```


### Success Response

**Code** : `200 OK`

**Content example**

```json
{
    "professor_link": "[inputed professor link]",
    "rating": (Float) rating of the professor.
}
```

### Error Response

**Condition** : Wrong input or professor not in database.

**Code** : `400 BAD REQUEST`

**Content** :

```json
{
    "err_msg": [
        "Unable to query the input professor."
    ]
}
```

# Student APIs
## Post Comments
Used to post a comment.

**URL** : `/api/student/post_comment/`

**Method** : `POST`

**Auth required** : YES

**Data constraints**

```json
{
    "professor_id": "[valid professor netid]",
    "course_code": "[valid course code]",
    "time": "[valid semester id]",
    "rating": (Int) rating for this comment,
    "comment_body": "[content body]"
}
```
### Success Response

**Code** : `200 OK`

### Error Response

**Condition** : Wrong information provided.

**Code** : `400 BAD REQUEST`

**Content** :

```json
{
    "err_msg": [
        "Unable to post comment, please check the given information."
    ]
}
```

## Hit Like/Disike
Used to handle like/dislike

**URL** : `/api/student/handle_like/`

**Method** : `POST`

**Auth required** : YES

**Data constraints**
```json
{
    "action": "plus" or "minus",
    "comment_id": "[corresponding comment id]"
}
```

### Success Response

**Code** : `200 OK`

### Error Response

**Condition** : Comment not in database or too frequent actions.

**Code** : `400 BAD REQUEST`

## Request Add Professor

Used to report the unvalid comments. 

**URL** : `/api/student/addprofessor`

**Method** : `POST`

**Auth required** : YES

**Data constraints**

```json
{
    "professor_email": "[the valid professor's email]",
    "professor_course": "[the couse the professor is teaching]"
}
```

### Success Response

**Code** : `200 OK`

### Error Response

**Condition** : The professor information is not valid.

**Code** : `400 BAD REQUEST`

**Content** :

```json
{
    "err_msg": "Add professor failure."
}
```

## Report Comments

Used to report the unvalid comments. 

**URL** : `/api/student/reportcomments`

**Method** : `POST`

**Auth required** : YES

**Data constraints**

```json
{
    "comment_id": "[the valid comment id]",
    "reason": "[the reason why report this comment in plain text]"
}
```

### Success Response

**Code** : `200 OK`

### Error Response

**Condition** : The reason is not reasonable. 

**Code** : `400 BAD REQUEST`

**Content** :

```json
{
    "err_msg": "Report failure."
}
```

# Professor APIs
## Course Statistics
Used to query statistics of a given course.

**URL** : `/api/professor/stats_course/`

**Method** : `GET`

**Auth required** : YES

**Data constraints**

```json
{
    "course_code": "[valid course_code]",
    "timespan": [
        "[start semester]",
        "[end semester]"
    ]
}
```

### Success Response

**Code** : `200 OK`

**Content Example**
```json
{
    "rating": (Float) rating of the given course,
    "total_num": "[number of ratings posted in the timespan]",
    "comments": [
        {
            "time": "[yyyy-mm-dd hh:mm]",
            "username": "[username of comment]",
            "course_code": "[valid course code]",
            "rating": (Float) rating of this comment, 
            "content": "[comment body]"
        }
    ]
}
```
### Error Response

**Condition** : If course has never been taught by the professor.

**Code** : `400 BAD REQUEST`

**Content** :

```json
{
    "err_msg": [
        "The account has existed. Falied to resgister."
    ]
}
```
# Administrator APIs

## Admin_Login

Used to authenticate for a registered Administrator.

**URL** : `/api/admin/login/`

**Method** : `POST`

**Auth required** : NO

**Data constraints**

```json
{
    "username": "[valid email address]",
    "password": "[password in plain text]"
}
```

### Success Response

**Code** : `200 OK`

**Content example**

```json
{
    "token": "[valid user token]"
}
```

### Error Response

**Condition** : If 'username' and 'password' combination is wrong.

**Code** : `400 BAD REQUEST`

**Content** :

```json
{
    "err_msg": [
        "Unable to login with provided credentials."
    ]
}
```

## Review_Comment

Used to review the new comments.

**URL** : `/api/admin/reviewcomment/`

**Method** : `POST`

**Auth required** : YES

**Data constraints**

```json
{
    "comment_id": "[valid comment id]",
    "validity": "[whether the comment is valid]"
}
```


### Success Response

**Code** : `200 OK`


## Approve_New_Professor_Request

Used to approve the new professor request from students.

**URL** : `/api/admin/approvenewprofessor/`

**Method** : `POST`

**Auth required** : YES

**Data constraints**

```json
{
    "professor_email": "[valid professor's email address]",
    "professor_course": "[the course of this professor]",
    "validity": "[whether the professor is valid]"
}
```

### Success Response

**Code** : `200 OK`

## Review_Report

Used to review the report of the comments.

**URL** : `/api/admin/reviewreport/`

**Method** : `POST`

**Auth required** : YES

**Data constraints**

```json
{
    "report_id": "[valid report id]",
    "comment_id": "[reproted comment id]",
    "validity": "[whether the report is valid]"
}
```

### Success Response

**Code** : `200 OK`
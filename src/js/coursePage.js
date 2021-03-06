import React from "react";
import {
    Layout,
    Breadcrumb,
    Input,
    Select,
    Row,
    Col,
    Form,
    List, Statistic
} from 'antd';
import "../css/profPage.css";
import MainHeader from "../components/common/header";
import MainFooter from "../components/common/footer";
import {
    LikeOutlined,
    StarOutlined,
    WarningOutlined, DownCircleOutlined, LikeTwoTone
} from "@ant-design/icons";
import { Button } from 'antd';
import { Rate } from 'antd'
import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Modal } from 'antd';
import http from "../services/httpService";

const { Content } = Layout;
const {Option} = Select;

const listData = {
    course_name:"",
    course_code:"",
    rating:5.0,
    comment:[],
    total_comments:0,
    offered_semesters:[]
}
for (let i = 0; i < listData.total_comments; i++) {
    listData.comment.push({
        time: "",
        username: "",
        rating: 5,
        content: "",
        likes: 0,
        dislikes: 0
    });
}

const IconText = ({ icon, text, onClick, style }) => (
    <span onClick={onClick} style={style}>
    {React.createElement(icon, { style: { marginRight: 8 } })}
        {text}
  </span>
);

class LikeBtn extends React.Component{
    /**
     * LikeBtn class，the like button for the course comment
     * @constructor
     * @param {object} state - whether the student like the comment or not and the current number of the likes for the comment
     */
    constructor(props){
        super(props)
        this.state={
            like:props.num,
            liked:null,
        };
    }
    islike =()=>{
        clearTimeout();
        /**
         * Like the course and change the number for the dislikes
         */
        let liked=this.state.liked;
        if(liked){
            if(liked === 'like'){
                this.setState({liked:null})
                this.setState({like:this.state.like-1});
            }
            else
            {
                this.setState({liked:'like'});
                this.setState({like:this.state.like+1});


            }
        }
        else {
            setTimeout(() => {
                http.post("student/handle_like", {comment_id: this.props.commentid, isLike: true}).then(response => {
                    if (response.data.code === 401){
                        alert("Please login!")
                    }
                    else{
                        this.setState({
                            like:this.state.like+1
                        });
                        this.setState({liked:'like'});
                    }
                })
            }, 200)
        }
    };
    render(){
        let icon = LikeOutlined;
        if (this.state.liked !== null){
            icon = LikeTwoTone;
        }
        return(
            <IconText
                onClick={this.islike}
                icon={icon}
                text={this.state.like}
                key="list-vertical-like-o"
                style={{width:60, cursor: "pointer"}}
            />
        );
    }
}

const Report = (props) => {
    const [form] = Form.useForm();
    const [isModalVisible, setIsModalVisible] = useState(false);

    const showModal = () => {
        console.log(props.commentid);
        setIsModalVisible(true);
    };

    const handleOk = () => {
        form.submit();
    };

    const onFinish = (values) => {
        values.comment_id = props.commentid;
        console.log("fin", values);
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(values),
            credentials: "include"
		}
        fetch("http://localhost:8081/student/reportcomment", requestOptions)
        .then(response => {
            if (response.status === 200){
                return response.json();
            }
            else{
                alert("Report failed, please try again!");
            }
        })
        .then(json => {
            if (json.code === 200){
                alert("Report Success!");
            }
            else{
                alert("Report failed, please try again!");
            }
            setIsModalVisible(false);
        })
        
    }

    const handleCancel = () => {
        setIsModalVisible(false);
    };

    return (
        <>
            <IconText icon={WarningOutlined} text="Report" onClick={showModal} style={{ cursor: "pointer"}}/>
            <Modal title="Report" visible={isModalVisible} onOk={handleOk} onCancel={handleCancel}>
                <p style={{fontWeight:"bolder", fontFamily:"GothamBook"}}>
                    Are you sure to report this comment?
                </p>
                <Form
                    form={form}
                    name="basic"
                    onFinish = {onFinish}
                    style={{fontSize:"3.0rem",fontWeight:"bolder", fontFamily:"GothamBook"}}
                >

                    <Form.Item
                        label="Report reason"
                        name="report_reason"
                        rules={[{ required: true, message: 'Please leave your reason!' }]}
                    >
                        <Input.TextArea bordered={false} placeholder="Leave your comment here!" style={{height:"80px",width:"350px"}}/>
                    </Form.Item>

                </Form>
            </Modal>
        </>
    );
};



class CoursePage extends React.Component{
    /**
     * CoursePage class, the information page for the course\
     * @constructor
     * @param {object} state - current course info
     */
    constructor(props){
        super(props)        
        let param = this.props.location.search.slice(1).split("=");
        this.state = {listData: listData, param:param};
        param[1] = param[1].replace("%20", " ");
        if (param[0] !== "v"){
            alert("Request not allowed!");
            window.location.href = "/";
        }
        console.log(param);
    }

    componentDidMount(){
        const requestOptions = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({course_code: this.state.param[1]}),
            credentials: "include"
			}
        fetch("http://localhost:8081/public/view_course", requestOptions)
        .then(response => {
            if (response.status === 200){
                return response.json();
            }
        })
        .then(json => {
            console.log(json);
            if (json.code === 200){
                console.log("setting");
                listData.course_name = json.course_name;
                listData.course_code = json.course_code;
                listData.rating = json.rating;
                listData.total_comments = json.comments_num;
                if (json.comments.length > 0){
                    for (let c of json.comments){
                        listData.comment.push({
                            time: c.date.split("T")[0],
                            comment_id: c.comment_id,
                            course_code:c.course_code,
                            username:c.student_id,
                            rating: c.rate,
                            content: c.content,
                            likes: c.likes,
                            dislikes: c.dislikes,
                        });
                    }
                }
                for (let s of json.offered_in){
                    listData.offered_semesters.push(s);
                }
                
                this.setState({listData: listData})
                console.log(this.state.listData)
            }
        })
    };

    onFinish = (values) => {
        values.course_code = this.state.param[1];
        const requestOption = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(values),
            credentials: "include"
        }
        fetch("http://localhost:8081/student/post_comment", requestOption).then(response => {
            if (response.status === 200){
                return response.json()
            }
            else if (response.status === 500){
                alert("You haven't logged in. Please login first!")
            }
            else{
                alert("Post failed, please try again.")
            }
        })
        .then(json => {
            if(json.code === 200){
                window.location.reload();
            }
            else if (json.code === 401) {
                alert("Please login first!");
            }
            else{
                alert("Post failed! Please try again.")
            }
        })
    }
    handleSelect = (values) =>{}

    handleReport = (event) => {
        console.log(event.target);
    }

    render(){
        let arr = listData.offered_semesters;
        return(
            <Layout className="layout" style={{minHeight: "100%"}}>
                <MainHeader />
                <Breadcrumb style={{margin:"20px 8%"}}>
                        <Breadcrumb.Item>
                            <Link to="/">Home</Link>
                        </Breadcrumb.Item>
                        <Breadcrumb.Item>
                            Course
                        </Breadcrumb.Item>
                </Breadcrumb>
                <Content className='ContentArea'>
                    <Row style={{marginTop:"10px"}}>

                        <Col span={8} offset={1} style={{paddingTop:"20px"}}>

                            <Row >
                                <h1 style={{fontSize:"2.0rem",fontWeight:"bolder", fontFamily:"GothamBook",width:"400px"}}>{listData.course_name} </h1>
                                <h3 style={{fontSize:"1rem",fontWeight:"bolder", fontFamily:"GothamBook",width:"400px"}}>{listData.course_code} </h3>
                                <Statistic style={{fontFamily:"GothamBook"}}
                                           value={listData.rating}
                                           suffix={" / 5 based on " + listData.total_comments +" comments"}
                                           valueStyle={{fontSize:"48px"}}
                                />
                            </Row>

                            <Row style={{marginTop:"40px"}}>
                                <h3 style={{fontSize:"1.0rem",fontWeight:"bolder",width:"400px"}}>
                                    Course is offered in:
                                </h3>

                                <ul style={{fontSize:"1.0rem",fontWeight:"normal",width:"400px"}}>
                                    {
                                        arr.map((item, index) => {
                                            return <li key={index}>{item}</li>
                                        })
                                    }
                                </ul>
                            </Row>
                            <h2 style={{fontSize:"1.0rem",width:"400px"}}>
                                    Rate This Course below
                                    <DownCircleOutlined style={{marginLeft: "10px"}}/>
                            </h2>
                            <h5 style={{width:"400px"}}>
                                    * Login required
                            </h5>


                            <Form
                                name="basic"
                                onFinish={this.onFinish}
                                style={{fontSize:"3.0rem",fontWeight:"bolder", fontFamily:"GothamBook"}}
                            >
                                <Form.Item
                                    label="Rate"
                                    name="rate"
                                    rules={[{ required: true, message: 'Please input your rate!' }]}
                                >
                                    <Rate allowHalf style={{marginLeft:"35px"}}/>
                                </Form.Item>

                                <Form.Item name="semester" label="Semester" rules={[
                                    {
                                        required: true,
                                        message: 'Please select a semester'
                                    },
                                    ]}>
                                    <Select
                                        placeholder="Select a semester"
                                        onChange={this.handleSelect}
                                    >
                                       {
                                        arr.map((item, index) => {
                                            return <Option value={item}>{item}</Option>
                                        })
                                        }
                                    </Select>
                                </Form.Item>

                                <Form.Item
                                    label="Comment"
                                    name="content"
                                    rules={[{ required: true, message: 'Please leave your comment!' }]}
                                >
                                    <Input.TextArea placeholder="Leave your comment here!" style={{height:"120px",width:"100%"}}/>
                                </Form.Item>

                                <Form.Item >
                                    <Button type="primary" htmlType="submit" >
                                        Submit
                                    </Button>
                                </Form.Item>
                            </Form>
                        </Col>

                        <Col span={13} offset={1}>

                            <List
                                itemLayout="vertical"
                                size="large"
                                pagination={{
                                    onChange: page => {
                                        console.log(page);
                                        document.body.scrollIntoView();
                                    },
                                    pageSize: 5,
                                }}
                                dataSource={listData.comment}

                                renderItem={item => (
                                    <List.Item
                                        className="listItemGeneral"
                                        style = {{marginTop:"15px",minHeight:"150px"}}
                                        key={item.title}
                                        actions={[
                                            <LikeBtn commentid={item.comment_id} num={item.likes}/>,
                                            <Report commentid={item.comment_id}/>
                                        ]}
                                        extra={
                                            <Row>
                                                <Col span={24}>
                                                    <Statistic title="Rate"
                                                               value={item.rating}
                                                               prefix={<StarOutlined />}
                                                               suffix=" / 5"
                                                               valueStyle={{fontSize:"40px",marginTop:"15px"}}
                                                    />

                                                </Col>
                                            </Row>
                                        }
                                    >
                                        <List.Item.Meta
                                            className="listItemMetaGeneral"
                                            title={<p commentid={item.comment_id}>{item.username}</p>}
                                            description={item.time}
                                        />
                                        {item.content}
                                    </List.Item>
                                )}
                            />


                        </Col>
                    </Row>
                </Content>
                <MainFooter />
            </Layout>


        )
    }
}

export default CoursePage;
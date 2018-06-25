package model;

public class Application {
    private String jobID;
    private String studentID;

    public Application(){

    }

    public Application(String jobID, String studentID) {
        this.jobID = jobID;
        this.studentID = studentID;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }
}

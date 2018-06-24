package model;

public class Vacancy {
    public String company_id, job_title, job_description, job_requirement, job_salary, intern_period, category_period, job_category;

    public Vacancy(){

    }

    public Vacancy( String title, String description, String requirement, String period, String salary, String id, String c_p, String category) {
        this.job_title = title;
        this.job_description = description;
        this.job_requirement = requirement;
        this.job_salary = salary;
        this.intern_period = period;
        this.company_id = id;
        this.category_period = c_p;
        this.job_category = category;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getJob_title() {
        return job_title;
    }

    public void setJob_title(String job_title) {
        this.job_title = job_title;
    }

    public String getJob_description() {
        return job_description;
    }

    public void setJob_description(String job_description) {
        this.job_description = job_description;
    }

    public String getJob_requirement() {
        return job_requirement;
    }

    public void setJob_requirement(String job_requirement) {
        this.job_requirement = job_requirement;
    }

    public String getJob_salary() {
        return job_salary;
    }

    public void setJob_salary(String job_salary) {
        this.job_salary = job_salary;
    }

    public String getIntern_period() {
        return intern_period;
    }

    public void setIntern_period(String intern_period) {
        this.intern_period = intern_period;
    }

    public String getCategory_period() {
        return category_period;
    }

    public void setCategory_period(String category_period) {
        this.category_period = category_period;
    }

    public String getJob_category() {
        return job_category;
    }

    public void setJob_category(String job_category) {
        this.job_category = job_category;
    }
}

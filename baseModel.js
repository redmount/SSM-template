/// Generated on 2019/02/01 21:44:54

const baseModel = {
    RTestTeacherTTestClazz(args) {
        const arg = args || {}
        this.pk = arg.pk || null  ///
        this.teacherPk = arg.teacherPk || null  ///教师pk
        this.clazzPk = arg.clazzPk || null  ///班级pk
        this.course = arg.course || null  ///此教师在此班级所上的课程名称    没有外关联关系表
        this.count = arg.count || null  ///此教师在此班级内的上课数量
        this.created = arg.created || null  ///
        this.updated = arg.updated || null  ///
    },
    TestClazz(args) {
        const arg = args || {}
        this.pk = arg.pk || null  ///
        this.name = arg.name || null  ///班级名称
        this.adviserPk = arg.adviserPk || null  ///班主任pk
        this.updated = arg.updated || null  ///
        this.created = arg.created || null  ///
        this.nickName = arg.nickName || null  ///班级昵称
        this.deleted = arg.deleted || null  ///是否被删除
    },
    TestClazzInfo(args) {
        const arg = args || {}
        this.pk = arg.pk || null  ///
        this.detail = arg.detail || null  ///
        this.clazzPk = arg.clazzPk || null  ///
        this.created = arg.created || null  ///
        this.updated = arg.updated || null  ///
    },
    TestStudent(args) {
        const arg = args || {}
        this.pk = arg.pk || null  ///
        this.name = arg.name || null  ///学生名称
        this.clazzPk = arg.clazzPk || null  ///所属班级pk
        this.updated = arg.updated || null  ///
        this.created = arg.created || null  ///
    },
    TestTeacher(args) {
        const arg = args || {}
        this.pk = arg.pk || null  ///
        this.name = arg.name || null  ///教师名称
        this.created = arg.created || null  ///
        this.updated = arg.updated || null  ///
    },
}
export default baseModel
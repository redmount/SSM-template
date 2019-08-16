/// Generated on 2019/08/15 12:53:29

const baseModel = {
    RTestTeacherTTestClazz(args) {
        const arg = args || {}
        this.pk = arg.pk || null  ///
        this.pk = arg.pk || null  ///
        this.teacherPk = arg.teacherPk || null  ///
        this.clazzPk = arg.clazzPk || null  ///
        this.course = arg.course || null  ///
        this.count = arg.count || null  ///
        this.created = arg.created || null  ///
        this.created = arg.created || null  ///
        this.updated = arg.updated || null  ///
        this.updated = arg.updated || null  ///
    },
    TestClazz(args) {
        const arg = args || {}
        this.pk = arg.pk || null  ///
        this.pk = arg.pk || null  ///
        this.name = arg.name || null  ///班级名称
        this.adviserPk = arg.adviserPk || null  ///班主任pk
        this.updated = arg.updated || null  ///
        this.updated = arg.updated || null  ///
        this.created = arg.created || null  ///
        this.created = arg.created || null  ///
        this.nickName = arg.nickName || null  ///
        this.detail = arg.detail || null  ///
    },
    TestStudent(args) {
        const arg = args || {}
        this.pk = arg.pk || null  ///
        this.pk = arg.pk || null  ///
        this.name = arg.name || null  ///学生名称
        this.clazzPk = arg.clazzPk || null  ///所属班级pk
        this.updated = arg.updated || null  ///
        this.updated = arg.updated || null  ///
        this.created = arg.created || null  ///
        this.created = arg.created || null  ///
    },
    TestTeacher(args) {
        const arg = args || {}
        this.pk = arg.pk || null  ///
        this.pk = arg.pk || null  ///
        this.name = arg.name || null  ///教师名称
        this.created = arg.created || null  ///
        this.created = arg.created || null  ///
        this.updated = arg.updated || null  ///
        this.updated = arg.updated || null  ///
    },
}
export default baseModel
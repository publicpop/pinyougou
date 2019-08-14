import com.health.constant.DateUtils;

public class test {

    public static void main(String[] args) throws Exception {
        //获取当前日期
        String today = DateUtils.parseDate2String(DateUtils.getToday());

        //获取本周一的日期
        String thisWeekMonday = DateUtils.parseDate2String(DateUtils.getThisWeekMonday());

        //获取本月的日期的第一天
        String firstDay4Month = DateUtils.parseDate2String(DateUtils.getFirstDay4ThisMonth());

        System.out.println("当前日期"+today+"本周第一天"+thisWeekMonday+"本月日期的第一天"+firstDay4Month);
    }
}

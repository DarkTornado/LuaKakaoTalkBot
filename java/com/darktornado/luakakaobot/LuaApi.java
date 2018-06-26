package com.darktornado.luakakaobot;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.Calendar;

public class LuaApi {

    static class Utils{

        static class GetWebText extends OneArgFunction {
            @Override
            public LuaValue call(LuaValue url) {
                String result = KakaoBot.getDataFromServer(url.tojstring());
                return LuaValue.valueOf(result);
            }
        }

        static class RemoveTags extends OneArgFunction{
            @Override
            public LuaValue call(LuaValue str) {
                String result = str.tojstring().replaceAll("(<([^>]+)>)", "");
                return LuaValue.valueOf(result);
            }
        }

    }

    static class Time{

        static class GetYear extends ZeroArgFunction{
            @Override
            public LuaValue call() {
                Calendar day = Calendar.getInstance();
                return LuaValue.valueOf(day.get(Calendar.YEAR));
            }
        }

        static class GetMonth extends ZeroArgFunction{
            @Override
            public LuaValue call() {
                Calendar day = Calendar.getInstance();
                return LuaValue.valueOf(day.get(Calendar.MONTH)+1);
            }
        }

        static class GetDate extends ZeroArgFunction{
            @Override
            public LuaValue call() {
                Calendar day = Calendar.getInstance();
                return LuaValue.valueOf(day.get(Calendar.DATE));
            }
        }

        static class GetDay extends ZeroArgFunction{
            @Override
            public LuaValue call() {
                Calendar day = Calendar.getInstance();
                int date = day.get(Calendar.DAY_OF_WEEK);
                switch(date){
                    case Calendar.MONDAY:
                        return LuaValue.valueOf("월");
                    case Calendar.TUESDAY:
                        return LuaValue.valueOf("화");
                    case Calendar.WEDNESDAY:
                        return LuaValue.valueOf("수");
                    case Calendar.THURSDAY:
                        return LuaValue.valueOf("목");
                    case Calendar.FRIDAY:
                        return LuaValue.valueOf("금");
                    case Calendar.SATURDAY:
                        return LuaValue.valueOf("토");
                    case Calendar.SUNDAY:
                        return LuaValue.valueOf("일");
                }
                return NIL;
            }
        }

        static class GetHour extends ZeroArgFunction{
            @Override
            public LuaValue call() {
                Calendar day = Calendar.getInstance();
                return LuaValue.valueOf(day.get(Calendar.HOUR));
            }
        }

        static class GetMinute extends ZeroArgFunction{
            @Override
            public LuaValue call() {
                Calendar day = Calendar.getInstance();
                return LuaValue.valueOf(day.get(Calendar.MINUTE));
            }
        }

        static class GetSecond extends ZeroArgFunction{
            @Override
            public LuaValue call() {
                Calendar day = Calendar.getInstance();
                return LuaValue.valueOf(day.get(Calendar.SECOND));
            }
        }

    }


}

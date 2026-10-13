package com.armin.deckx;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.content.pm.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import org.json.*;
import java.util.*;

public class MainActivity extends Activity {
    private static final int BG=Color.rgb(10,12,14), PANEL=Color.rgb(24,28,31), GREEN=Color.rgb(170,255,0), WHITE=Color.rgb(239,242,238), MUTED=Color.rgb(150,160,154);
    private LinearLayout root, body, nav;
    private android.content.SharedPreferences prefs;
    private ArrayList<ResolveInfo> games=new ArrayList<>();
    private String page="home";
    @Override public void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(BG); getWindow().setNavigationBarColor(BG); prefs=getSharedPreferences("deckx",0); show("home");}
    private GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); return d;}
    private TextView text(String s,int size,int color){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);t.setGravity(Gravity.CENTER_VERTICAL);return t;}
    private TextView button(String s,Runnable r){TextView t=text(s,15,WHITE);t.setGravity(Gravity.CENTER);t.setPadding(14,14,14,14);t.setBackground(shape(PANEL,18));t.setOnClickListener(v->r.run());return t;}
    private void add(LinearLayout p,View v,int h){p.addView(v,new LinearLayout.LayoutParams(-1,h));}
    private void gap(LinearLayout p,int h){View v=new View(this);p.addView(v,new LinearLayout.LayoutParams(1,h));}
    private void label(String s){TextView t=text(s,13,GREEN);t.setTypeface(null,Typeface.BOLD);add(body,t,34);}
    private void show(String target){
        page=target; root=new LinearLayout(this);root.setOrientation(1);root.setPadding(20,18,20,12);root.setBackgroundColor(BG);setContentView(root);
        TextView brand=text("ARMIN DECK X   /   GAMING SYSTEM",16,GREEN);brand.setTypeface(null,Typeface.BOLD);add(root,brand,38);
        TextView sub=text("YOUR GAMES. YOUR ARENA.",11,MUTED);add(root,sub,24);gap(root,10);
        ScrollView scroll=new ScrollView(this);body=new LinearLayout(this);body.setOrientation(1);scroll.addView(body);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        if(target.equals("home")) home(); else if(target.equals("games")) library(); else if(target.equals("tournaments")) tournaments(); else if(target.equals("profile")) profile(); else settings();
        gap(root,10);nav=new LinearLayout(this);nav.setOrientation(0);String[][] items={{"خانه","home"},{"بازی‌ها","games"},{"مسابقات","tournaments"},{"پروفایل","profile"},{"تنظیمات","settings"}};
        for(String[] it:items){TextView t=text(it[0],12,target.equals(it[1])?GREEN:WHITE);t.setGravity(Gravity.CENTER);t.setPadding(3,13,3,13);t.setBackground(shape(PANEL,12));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,48,1);lp.setMargins(2,0,2,0);nav.addView(t,lp);t.setOnClickListener(v->show(it[1]));}root.addView(nav);
    }
    private void home(){
        gap(body,10);TextView hero=text("GAME MODE  /  READY",13,GREEN);hero.setPadding(18,0,18,0);hero.setBackground(shape(PANEL,20));add(body,hero,54);gap(body,18);
        TextView title=text("به داشبوردت خوش آمدی",24,WHITE);title.setTypeface(null,Typeface.BOLD);add(body,title,42);
        TextView desc=text("کتابخانه بازی‌ها و رقابت‌های محلی‌ات را مدیریت کن.",14,MUTED);add(body,desc,34);gap(body,18);
        int count=scanGames().size(); rowStat("بازی‌های قابل شناسایی",String.valueOf(count));rowStat("مسابقات ذخیره‌شده",String.valueOf(loadTournaments().length()));
        gap(body,18);label("دسترسی سریع");add(body,button("▦  کتابخانه بازی‌ها",()->show("games")),56);gap(body,8);add(body,button("⚔  ساخت مسابقه آفلاین",()->show("tournaments")),56);
        gap(body,18);label("وضعیت سیستم");TextView status=text("حالت آفلاین فعال است  •  داده‌ها روی همین دستگاه ذخیره می‌شوند",12,MUTED);add(body,status,44);
    }
    private void rowStat(String name,String value){LinearLayout row=new LinearLayout(this);row.setPadding(15,8,15,8);row.setGravity(Gravity.CENTER_VERTICAL);row.setBackground(shape(PANEL,16));TextView a=text(name,14,WHITE);row.addView(a,new LinearLayout.LayoutParams(0,48,1));TextView b=text(value,22,GREEN);b.setTypeface(null,Typeface.BOLD);row.addView(b);LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=8;body.addView(row,lp);}
    private ArrayList<ResolveInfo> scanGames(){
        ArrayList<ResolveInfo> out=new ArrayList<>();Intent i=new Intent(Intent.ACTION_MAIN);i.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm=getPackageManager(); List<ResolveInfo> all=pm.queryIntentActivities(i,0);
        for(ResolveInfo r:all) if(r.activityInfo!=null && !r.activityInfo.packageName.equals(getPackageName())) out.add(r);
        Collections.sort(out,(a,b)->a.loadLabel(pm).toString().compareToIgnoreCase(b.loadLabel(pm).toString()));return out;
    }
    private void library(){
        label("کتابخانه بازی‌ها");TextView info=text("برنامه‌هایی که اندروید اجازه شناسایی آن‌ها را می‌دهد.",12,MUTED);add(body,info,42);
        EditText search=new EditText(this);search.setSingleLine(true);search.setTextColor(WHITE);search.setHintTextColor(MUTED);search.setHint("جست‌وجوی بازی...");search.setBackground(shape(PANEL,14));search.setPadding(14,0,14,0);add(body,search,50);gap(body,10);
        LinearLayout list=new LinearLayout(this);list.setOrientation(1);add(body,list,-2);
        games=scanGames(); Runnable render=()->{list.removeAllViews();String q=search.getText().toString().toLowerCase(Locale.ROOT);int shown=0;
            for(ResolveInfo r:games){String name=r.loadLabel(getPackageManager()).toString();if(!name.toLowerCase(Locale.ROOT).contains(q))continue;shown++;
                LinearLayout row=new LinearLayout(this);row.setPadding(10,8,10,8);row.setGravity(Gravity.CENTER_VERTICAL);row.setBackground(shape(PANEL,14));ImageView icon=new ImageView(this);try{icon.setImageDrawable(r.loadIcon(getPackageManager()));}catch(Exception ignored){}row.addView(icon,new LinearLayout.LayoutParams(44,44));
                TextView label=text(name,14,WHITE);label.setPadding(12,0,8,0);row.addView(label,new LinearLayout.LayoutParams(0,52,1));TextView launch=text("اجرا",13,GREEN);row.addView(launch);launch.setOnClickListener(v->{try{Intent in=new Intent(Intent.ACTION_MAIN);in.addCategory(Intent.CATEGORY_LAUNCHER);in.setClassName(r.activityInfo.packageName,r.activityInfo.name);in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(in);}catch(Exception e){new AlertDialog.Builder(this).setMessage("این برنامه قابل اجرا نیست.").setPositiveButton("باشه",null).show();}});
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=7;list.addView(row,lp);
            } if(shown==0){TextView empty=text(games.isEmpty()?"برنامه‌ای برای نمایش پیدا نشد.":"نتیجه‌ای برای این جست‌وجو نیست.",14,MUTED);add(list,empty,70);}
        };search.addTextChangedListener(new android.text.TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int c,int a){}public void onTextChanged(CharSequence s,int st,int before,int count){render.run();}public void afterTextChanged(android.text.Editable e){}});render.run();
    }
    private JSONArray loadTournaments(){try{return new JSONArray(prefs.getString("tournaments","[]"));}catch(Exception e){return new JSONArray();}}
    private void saveTournaments(JSONArray a){prefs.edit().putString("tournaments",a.toString()).apply();}
    private void tournaments(){
        label("مسابقات آفلاین");add(body,text("مسابقه حذفی بساز، نتایج را ثبت کن و برنده را مشخص کن.",13,MUTED),44);gap(body,8);
        add(body,button("＋  ساخت مسابقه جدید",()->createTournament()),52);gap(body,14);
        JSONArray arr=loadTournaments();if(arr.length()==0){TextView empty=text("هنوز مسابقه‌ای نساخته‌ای.\nبا ساخت مسابقه، شرکت‌کنندگان و جدول در همین‌جا ذخیره می‌شوند.",14,MUTED);empty.setGravity(Gravity.CENTER);empty.setBackground(shape(PANEL,18));add(body,empty,112);return;}
        for(int i=arr.length()-1;i>=0;i--){JSONObject o=arr.optJSONObject(i);if(o==null)continue;final int index=i;LinearLayout card=new LinearLayout(this);card.setOrientation(1);card.setPadding(14,10,14,10);card.setBackground(shape(PANEL,16));TextView title=text(o.optString("name","مسابقه"),17,WHITE);add(card,title,34);add(card,text("شرکت‌کنندگان: "+o.optString("players",""),12,MUTED),28);
            add(card,button("نمایش جدول و ثبت نتیجه",()->showBracket(index)),46);add(card,button("حذف مسابقه",()->new AlertDialog.Builder(this).setMessage("این مسابقه حذف شود؟").setNegativeButton("لغو",null).setPositiveButton("حذف",(d,w)->{JSONArray a=loadTournaments();a.remove(index);saveTournaments(a);show("tournaments");}).show()),42);
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=10;body.addView(card,lp);
        }
    }
    private void createTournament(){
        LinearLayout form=new LinearLayout(this);form.setPadding(5,5,5,5);form.setOrientation(1);EditText name=new EditText(this);name.setHint("نام مسابقه");form.addView(name);EditText players=new EditText(this);players.setHint("نام شرکت‌کنندگان با ویرگول جدا شوند");players.setMinLines(2);form.addView(players);
        new AlertDialog.Builder(this).setTitle("مسابقه جدید").setView(form).setNegativeButton("لغو",null).setPositiveButton("ساخت",(d,w)->{
            String n=name.getText().toString().trim(), raw=players.getText().toString().trim();String[] ps=raw.split("[,،\n]+");ArrayList<String> clean=new ArrayList<>();for(String p:ps)if(!p.trim().isEmpty()&&!clean.contains(p.trim()))clean.add(p.trim());
            if(n.isEmpty()||clean.size()<2){new AlertDialog.Builder(this).setMessage("نام مسابقه و حداقل دو شرکت‌کننده لازم است.").setPositiveButton("باشه",null).show();return;}
            JSONObject o=new JSONObject();try{o.put("name",n);o.put("players",android.text.TextUtils.join("، ",clean));o.put("rounds",makeRounds(clean));JSONArray a=loadTournaments();a.put(o);saveTournaments(a);}catch(Exception ignored){}show("tournaments");
        }).show();
    }
    private JSONArray makeRounds(ArrayList<String> players)throws Exception{
        JSONArray rounds=new JSONArray();ArrayList<String> cur=new ArrayList<>(players);int size=1;while(size<cur.size())size*=2;
        while(cur.size()<size)cur.add("استراحت");JSONArray first=new JSONArray();
        for(int i=0;i<cur.size();i+=2){JSONObject m=new JSONObject();m.put("a",cur.get(i));m.put("b",cur.get(i+1));m.put("winner",cur.get(i+1).equals("استراحت")?cur.get(i):"");first.put(m);}rounds.put(first);return rounds;
    }
    private void showBracket(int index){
        JSONArray all=loadTournaments();JSONObject tournament=all.optJSONObject(index);if(tournament==null)return;JSONArray rounds=tournament.optJSONArray("rounds");if(rounds==null)return;
        LinearLayout wrap=new LinearLayout(this);wrap.setOrientation(1);ScrollView sc=new ScrollView(this);sc.addView(wrap);
        for(int ri=0;ri<rounds.length();ri++){JSONArray round=rounds.optJSONArray(ri);if(round==null)continue;TextView heading=text(ri==0?"دور اول":"مرحله "+(ri+1),15,GREEN);wrap.addView(heading);
            for(int mi=0;mi<round.length();mi++){JSONObject m=round.optJSONObject(mi);if(m==null)continue;String a=m.optString("a"),b=m.optString("b"),win=m.optString("winner");TextView match=text(a+"  مقابل  "+b+"\nبرنده: "+(win.isEmpty()?"ثبت نشده":win),14,WHITE);match.setPadding(10,10,10,10);wrap.addView(match);
                if(!b.equals("استراحت")&&win.isEmpty()){final JSONObject mm=m;String[] choices={a,b};new AlertDialog.Builder(this).setTitle("ثبت برنده").setItems(choices,(d,which)->{mm.optString("winner");try{mm.put("winner",choices[which]);all.put(index,tournament);saveTournaments(all);showBracket(index);}catch(Exception ignored){}}).show();}
            }
        }
        new AlertDialog.Builder(this).setTitle(tournament.optString("name")).setView(sc).setPositiveButton("بستن",null).show();
    }
    private void profile(){label("پروفایل بازیکن");EditText nick=new EditText(this);nick.setSingleLine(true);nick.setTextColor(WHITE);nick.setHintTextColor(MUTED);nick.setHint("نام مستعار");nick.setText(prefs.getString("nickname",""));nick.setBackground(shape(PANEL,14));nick.setPadding(14,0,14,0);add(body,nick,52);gap(body,10);add(body,button("ذخیره نام",()->{prefs.edit().putString("nickname",nick.getText().toString().trim()).apply();Toast.makeText(this,"نام ذخیره شد",Toast.LENGTH_SHORT).show();}),50);gap(body,20);rowStat("مسابقات ساخته‌شده",String.valueOf(loadTournaments().length()));add(body,text("آمار فقط از داده‌های ذخیره‌شده در همین دستگاه محاسبه می‌شود.",12,MUTED),48);}
    private void settings(){label("تنظیمات");add(body,text("ARMIN DECK X • نسخه 1.0.0",16,WHITE),42);add(body,text("ذخیره‌سازی محلی • بدون حساب کاربری • بدون ردیابی",12,MUTED),38);gap(body,10);add(body,button("پاک‌کردن تمام داده‌های محلی",()->new AlertDialog.Builder(this).setMessage("نام بازیکن و مسابقات ذخیره‌شده حذف شوند؟").setNegativeButton("لغو",null).setPositiveButton("حذف",(d,w)->{prefs.edit().clear().apply();show("settings");}).show()),54);gap(body,12);add(body,text("این نسخه اولیه است. پیش از انتشار عمومی، جدول چندمرحله‌ای مسابقات، آزمون‌های خودکار و بررسی سیاست‌های توزیع بازی‌ها باید تکمیل شوند.",12,MUTED),90);}
}

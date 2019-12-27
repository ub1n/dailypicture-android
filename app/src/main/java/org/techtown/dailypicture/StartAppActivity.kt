package org.techtown.dailypicture

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_start_app.*
import org.techtown.dailypicture.Retrofit.Request.RegisterRequest
import org.techtown.dailypicture.Retrofit.Response.RegisterResponse
import org.techtown.dailypicture.utils.TokenTon
import org.techtown.kotlin_todolist.RetrofitGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class StartAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_app)

        var terms_agree_3: Int = 0 //전체 동의
        /*textView14.movementMethod = ScrollingMovementMethod()
        textView14.text="*<오레오>('Prography.org'이하 '데일리픽쳐')*은(는) 개인정보보호법에 따라 이용자의 개인정보 보호 및 권익을 보호하고 개인정보와 관련한 이용자의 고충을 원활하게 처리할 수 있도록 다음과 같은 처리방침을 두고 있습니다.\n" +
                "\n" +
                "*<오레오>('데일리픽쳐')* 은(는) 회사는 개인정보처리방침을 개정하는 경우 웹사이트 공지사항(또는 개별공지)을 통하여 공지할 것입니다.\n" +
                "\n" +
                "○ 본 방침은부터 *2019*년 *12*월 *20*일부터 시행됩니다.\n" +
                "\n" +
                "**1. 개인정보의 처리 목적 *<오레오>('Prography.org'이하 '데일리픽쳐')*은(는) 개인정보를 다음의 목적을 위해 처리합니다. 처리한 개인정보는 다음의 목적이외의 용도로는 사용되지 않으며 이용 목적이 변경될 시에는 사전동의를 구할 예정입니다.**\n" +
                "\n" +
                "가. 홈페이지 회원가입 및 관리\n" +
                "\n" +
                "회원 가입의사 확인, 회원제 서비스 제공에 따른 본인 식별·인증 등을 목적으로 개인정보를 처리합니다.\n" +
                "\n" +
                "**2. 개인정보 파일 현황**\n" +
                "\n" +
                "1. 개인정보 파일명 : 데일리픽쳐 개인정보처리방침- 개인정보 항목 : 이메일, 휴대전화번호, 비밀번호, 로그인ID, 성별, 생년월일, 이름, 서비스 이용 기록, 접속 로그, 쿠키, 결제기록- 수집방법 : 홈페이지- 보유근거 : 홈페이지 회원정보 수집 등에 관한 기록- 보유기간 : 3년- 관련법령 : 신용정보의 수집/처리 및 이용 등에 관한 기록 : 3년\n" +
                "\n" +
                "**3. 개인정보의 처리 및 보유 기간**① *<오레오>('데일리픽쳐')*은(는) 법령에 따른 개인정보 보유·이용기간 또는 정보주체로부터 개인정보를 수집시에 동의 받은 개인정보 보유,이용기간 내에서 개인정보를 처리,보유합니다.② 각각의 개인정보 처리 및 보유 기간은 다음과 같습니다.\n" +
                "\n" +
                "1.<홈페이지 회원가입 및 관리><홈페이지 회원가입 및 관리>와 관련한 개인정보는 수집.이용에 관한 동의일로부터<3년>까지 위 이용목적을 위하여 보유.이용됩니다.-보유근거 : 홈페이지 회원정보 수집 등에 관한 기록-관련법령 : 신용정보의 수집/처리 및 이용 등에 관한 기록 : 3년-예외사유 :\n" +
                "\n" +
                "**4. 정보주체와 법정대리인의 권리·의무 및 그 행사방법 이용자는 개인정보주체로써 다음과 같은 권리를 행사할 수 있습니다.**\n" +
                "\n" +
                "① 정보주체는 오레오에 대해 언제든지 개인정보 열람,정정,삭제,처리정지 요구 등의 권리를 행사할 수 있습니다.② 제1항에 따른 권리 행사는오레오에 대해 개인정보 보호법 시행령 제41조제1항에 따라 서면, 전자우편, 모사전송(FAX) 등을 통하여 하실 수 있으며 오레오은(는) 이에 대해 지체 없이 조치하겠습니다.③ 제1항에 따른 권리 행사는 정보주체의 법정대리인이나 위임을 받은 자 등 대리인을 통하여 하실 수 있습니다. 이 경우 개인정보 보호법 시행규칙 별지 제11호 서식에 따른 위임장을 제출하셔야 합니다.④ 개인정보 열람 및 처리정지 요구는 개인정보보호법 제35조 제5항, 제37조 제2항에 의하여 정보주체의 권리가 제한 될 수 있습니다.⑤ 개인정보의 정정 및 삭제 요구는 다른 법령에서 그 개인정보가 수집 대상으로 명시되어 있는 경우에는 그 삭제를 요구할 수 없습니다.⑥ 오레오은(는) 정보주체 권리에 따른 열람의 요구, 정정·삭제의 요구, 처리정지의 요구 시 열람 등 요구를 한 자가 본인이거나 정당한 대리인인지를 확인합니다.\n" +
                "\n" +
                "**5. 처리하는 개인정보의 항목 작성**① *<오레오>('Prography.org'이하 '데일리픽쳐')*은(는) 다음의 개인정보 항목을 처리하고 있습니다.\n" +
                "\n" +
                "1<홈페이지 회원가입 및 관리>- 필수항목 : 이메일, 비밀번호, 로그인ID, 생년월일, 이름, 서비스 이용 기록, 접속 로그, 쿠키, 결제기록- 선택항목 : 휴대전화번호\n" +
                "\n" +
                "**6. 개인정보의 파기*<오레오>('데일리픽쳐')*은(는) 원칙적으로 개인정보 처리목적이 달성된 경우에는 지체없이 해당 개인정보를 파기합니다. 파기의 절차, 기한 및 방법은 다음과 같습니다.**\n" +
                "\n" +
                "-파기절차이용자가 입력한 정보는 목적 달성 후 별도의 DB에 옮겨져(종이의 경우 별도의 서류) 내부 방침 및 기타 관련 법령에 따라 일정기간 저장된 후 혹은 즉시 파기됩니다. 이 때, DB로 옮겨진 개인정보는 법률에 의한 경우가 아니고서는 다른 목적으로 이용되지 않습니다.-파기기한이용자의 개인정보는 개인정보의 보유기간이 경과된 경우에는 보유기간의 종료일로부터 5일 이내에, 개인정보의 처리 목적 달성, 해당 서비스의 폐지, 사업의 종료 등 그 개인정보가 불필요하게 되었을 때에는 개인정보의 처리가 불필요한 것으로 인정되는 날로부터 5일 이내에 그 개인정보를 파기합니다.\n" +
                "\n" +
                "-파기방법전자적 파일 형태의 정보는 기록을 재생할 수 없는 기술적 방법을 사용합니다.\n" +
                "\n" +
                "**7. 개인정보 자동 수집 장치의 설치•운영 및 거부에 관한 사항**\n" +
                "\n" +
                "① 오레오 은 개별적인 맞춤서비스를 제공하기 위해 이용정보를 저장하고 수시로 불러오는 ‘쿠기(cookie)’를 사용합니다. ② 쿠키는 웹사이트를 운영하는데 이용되는 서버(http)가 이용자의 컴퓨터 브라우저에게 보내는 소량의 정보이며 이용자들의 PC 컴퓨터내의 하드디스크에 저장되기도 합니다. 가. 쿠키의 사용 목적 : 이용자가 방문한 각 서비스와 웹 사이트들에 대한 방문 및 이용형태, 인기 검색어, 보안접속 여부, 등을 파악하여 이용자에게 최적화된 정보 제공을 위해 사용됩니다. 나. 쿠키의 설치•운영 및 거부 : 웹브라우저 상단의 도구>인터넷 옵션>개인정보 메뉴의 옵션 설정을 통해 쿠키 저장을 거부 할 수 있습니다. 다. 쿠키 저장을 거부할 경우 맞춤형 서비스 이용에 어려움이 발생할 수 있습니다.\n" +
                "\n" +
                "**8. 개인정보 보호책임자 작성**\n" +
                "\n" +
                "① 오레오(‘Prography.org’이하 ‘데일리픽쳐) 은(는) 개인정보 처리에 관한 업무를 총괄해서 책임지고, 개인정보 처리와 관련한 정보주체의 불만처리 및 피해구제 등을 위하여 아래와 같이 개인정보 보호책임자를 지정하고 있습니다.\n" +
                "\n" +
                "▶ 개인정보 보호책임자성명 :오레오직책 :팀직급 :팀연락처 :없음, 없음, 없음※ 개인정보 보호 담당부서로 연결됩니다.▶ 개인정보 보호 담당부서부서명 :담당자 :연락처 :, ,② 정보주체께서는 오레오(‘Prography.org’이하 ‘데일리픽쳐) 의 서비스(또는 사업)을 이용하시면서 발생한 모든 개인정보 보호 관련 문의, 불만처리, 피해구제 등에 관한 사항을 개인정보 보호책임자 및 담당부서로 문의하실 수 있습니다. 오레오(‘Prography.org’이하 ‘데일리픽쳐) 은(는) 정보주체의 문의에 대해 지체 없이 답변 및 처리해드릴 것입니다.\n" +
                "\n" +
                "**9. 개인정보 처리방침 변경**\n" +
                "\n" +
                "①이 개인정보처리방침은 시행일로부터 적용되며, 법령 및 방침에 따른 변경내용의 추가, 삭제 및 정정이 있는 경우에는 변경사항의 시행 7일 전부터 공지사항을 통하여 고지할 것입니다.\n" +
                "\n" +
                "**10. 개인정보의 안전성 확보 조치 *<오레오>('데일리픽쳐')*은(는) 개인정보보호법 제29조에 따라 다음과 같이 안전성 확보에 필요한 기술적/관리적 및 물리적 조치를 하고 있습니다.**\n" +
                "\n" +
                "1. 정기적인 자체 감사 실시개인정보 취급 관련 안정성 확보를 위해 정기적(분기 1회)으로 자체 감사를 실시하고 있습니다."*/
        //글씨 밑에 밑줄
        text_require1.setOnClickListener {
            var intent=Intent(Intent.ACTION_VIEW, Uri.parse("https://www.notion.so/prographyoreo/2c09c40094ed46cb9930718678a41e93"))
            startActivity(intent)
        }
        text_require1.getPaint().setUnderlineText(true)
        //text_require2.getPaint().setUnderlineText(true)

        //이전에 실행한 기록 있는지 SharedPreference로 저장
        val save = getSharedPreferences("save", Context.MODE_PRIVATE)
        val saveEditor = save.edit()
        val terms_agree_4 = save.getString("agree", "")

        val uuidSP=getSharedPreferences("uuid",Context.MODE_PRIVATE)
        val uuidEditor=uuidSP.edit()
        val getuuid=uuidSP.getString("uuid","")
        val uuid=getUuid()
        TokenTon.setuuid(uuid)

        //이전 실행기록이 있는지 확인하는 것
        if (terms_agree_4 == "all agree"&& getuuid!=null) {
            val intent = Intent(this, LoadingActivity::class.java)
            //Toast.makeText(this,getuuid,Toast.LENGTH_LONG).show()
            intent.putExtra("uuid",getuuid)
            startActivity(intent)
            finish()
        } else {
            //전체동의 체크박스
            checkBox3.setOnClickListener(View.OnClickListener {
                terms_agree_3 = 1
            }
            )

            //시작하기 버튼
            btn_start.setOnClickListener {
                if (terms_agree_3 == 1) {
                            saveEditor.putString("agree", "all agree")
                            saveEditor.commit()

                            RegisterServer(uuid,uuid)
                            uuidEditor.putString("uuid",uuid)
                            uuidEditor.commit()
                            //user.uuid=uuid
                            //val database:UserDatabase=UserDatabase.getInstance(applicationContext)
                            //val userDao: UserDao =database.userDao
                            //Thread{database.userDao.insert(user)}.start()

                            var intent = Intent(this, LoadingActivity::class.java)
                            intent.putExtra("uuid",getuuid)
                            intent.putExtra("status",1)

                            //uuid를 전달해준다. 이 값을 기억해야함!
                            //intent.putExtra("uuid",uuid);
                            startActivityForResult(intent, 2)
                            finish()
                        } else {
                            Toast.makeText(applicationContext, "약관을 체크해주세요", Toast.LENGTH_LONG)
                                .show()
                        }
            }
        }
    }

    //UUID값 받아오기
    private fun getUuid():String{
        //UUID를 생성하지만 랜덤으로 생성되기 때문에 내부 저장소에 저장해두어야함
        //사용자가 내부저장소를 지우거나 앱을 삭제 후 재설치하는 경우 ID가 달라질 수 있음
        val uuid= UUID.randomUUID().toString()

        return uuid
    }

    //Retrofit 서버 연결
    private fun RegisterServer(username:String,password:String){
        val userRequest= RegisterRequest(username)
        val call= RetrofitGenerator.create().registerUser(userRequest)
        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                //토큰 값 받아오기
                //Toast.makeText(this@StartAppActivity,response.body()?.uuid.toString(), Toast.LENGTH_LONG).show()
                //TokenTon.setuuid(response.body()?.uuid.toString())

            }
            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {

            }

        })

    }


}

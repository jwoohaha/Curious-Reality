<template>
  <div class="signup">
    <div class="head">회원정보 수정</div>
    <form @submit.prevent="submitForm">
      <div class="input-body">
        <div class="input-head"><b>기존 비밀번호</b></div>
        <input type="password" placeholder="PASSWORD" v-model="this.userInfo.password" />
      </div>
      <div class="input-body">
        <div class="input-head"><b>비밀번호</b></div>
        <input type="password" placeholder="PASSWORD" v-model="this.userInfo.newPassword" />
        <div class="alert-message" v-show="!isPasswordValid && this.userInfo.newPassword !== ''">
          8, 20자 사이의 영어, 숫자, 특수문자를 사용하여 생성해주세요.
        </div>
      </div>
      <div class="input-body">
        <div class="input-head"><b>비밀번호 확인</b></div>
        <input type="password" placeholder="PASSWORD" v-model="this.userInfo.newPasswordCheck" />
        <div class="alert-message" v-show="!isPasswordCheckValid && this.userInfo.newPasswordCheck !== ''">
          비밀번호가 일치하지 않습니다.
        </div>
      </div>
      <div class="input-body">
        <div class="input-head"><b>전화번호</b></div>
        <input type="text" placeholder="CONTACT" v-model="this.userInfo.contact" />
        <div class="alert-message" v-show="!isContactValid && this.userInfo.contact !== ''">
          올바른 전화번호 형식이 아닙니다. 010-0000-0000
        </div>
      </div>
      <button :disabled="!isValidEdit" type="submit" class="signup-button">수정완료</button>
    </form>

    <a href="/deleteUser" class="delete-user">회원탈퇴</a>
  </div>
</template>

<script>
import { editUserProfile, getNewAccessToken } from "@/api/userApi";
import { validateContact, validatePassword } from "@/utils/validation"; // 유효성 검사
export default {
  name: "EditProfileView",
  data() {
    return {
      userInfo: {
        password: '',
        newPassword: null,
        newPasswordCheck: null,
        contact: null
      }
    };
  },
  computed: {
    // 유효성 검사
    isContactValid() {
      return validateContact(this.userInfo.contact);
    },
    isPasswordValid() {
      return validatePassword(this.userInfo.newPassword);
    },
    isPasswordCheckValid() {
      return this.userInfo.newPassword === this.userInfo.newPasswordCheck;
    },
    isValidEdit() {
      if (this.userInfo.password == "") return false;
      return (
        (this.isPasswordValid &&
          this.isPasswordCheckValid) ||
        this.isContactValid
      );
    },
  },
  created() {
    this.checkLogin();
  },
  methods: {
    async submitForm() {
      if (this.isValidEdit) {
        const memberData = {
          password: this.userInfo.password,
          newPassword: this.userInfo.newPassword,
          newPasswordCheck: this.userInfo.newPasswordCheck,
          contact: this.userInfo.contact,
        };
        try {
          await editUserProfile(memberData);
          alert(`수정되었습니다`);

          window.location.href = "/";
        } catch (error) {
          alert("수정에 실패했습니다.");
          console.error(error.message);
        }
      }
    },
    async getNewAccessToken() {
      try {
        const { data } = await getNewAccessToken();
        console.log(data);
        localStorage.setItem("userToken", data.accessToken);
      } catch (error) {
        if (error.response && error.response.status === 401) {
          alert("로그인 후 이용해주세요.");
          localStorage.removeItem("userEmail");
          localStorage.removeItem("userToken");
          window.location.href = "/";
        } else if (error.response && error.response.status === 601) {
          alert("로그인이 만료되었습니다.");
          localStorage.removeItem("userEmail");
          localStorage.removeItem("userToken");
          window.location.href = "/";
        }
      }
    },
    checkLogin() {
      const email = localStorage.getItem("userEmail");
      if (!email) {
        alert("로그인 후 이용해주세요!");
        window.location.href = "/";
      }
    }
  },
};
</script>

<style scoped>
.signup {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

.head {
  display: flex;
  width: 500px;
  height: 108px;
  flex-direction: column;
  justify-content: center;
  flex-shrink: 0;
  color: #000;
  text-align: center;
  font-family: Noto Sans KR;
  font-size: 30px;
  font-style: normal;
  font-weight: 500;
  line-height: normal;
  letter-spacing: 6.24px;
}

.input-body {
  width: 500px;
  height: 100px;
  justify-content: center;
  flex-shrink: 0;
  margin-bottom: 30px;
}

.input-head {
  color: #000;
  padding: 10px;
  font-family: Noto Sans KR;
  font-size: 15px;
  font-style: normal;
  font-weight: 500;
  line-height: normal;
  letter-spacing: 2px;
}

input {
  padding: 23px 20px 22px 20px;
  width: 90%;
  align-items: center;
  flex-shrink: 0;
  border-radius: 1.5rem;
  border: 1px solid #a8a8a8;
}

.signup-button {
  width: 200px;
  height: 50px;
  background: #1f2db1;
  font-family: "Noto Sans KR";
  font-style: normal;
  font-weight: 500;
  font-size: 1.2rem;
  line-height: 43px;
  display: flex;
  align-items: center;
  justify-content: center;
  letter-spacing: 0.13rem;
  color: #ffffff;
  border-radius: 26px;
  cursor: pointer;
  border-radius: 1rem;
  margin: auto;
}

.signup-button:disabled {
  background-color: #cccccc;
}

.check-button {
  display: inline-flex;
  padding: 10px 15px 10px 15px;
  justify-content: center;
  align-items: center;
  border-radius: 20px;
  background: #1f2db1;
  color: #fff;
  font-family: Noto Sans KR;
  font-size: 20px;
  font-style: normal;
  letter-spacing: 3.9px;
}

.checkbox-interest {
  display: inline-flex;
  margin-top: 1rem;
}

.checkbox-interest>input {
  width: 20px;
  margin: 0px 10px 0px 14px;
  align-items: center;
  vertical-align: middle;

  color: #000;
  font-family: "Noto Sans KR";
  font-size: 20px;
  font-style: normal;
  text-align: center;
}

.alert-message {
  margin-top: 5px;
  margin-left: 15px;
  color: red;
}

.delete-user {
  cursor: pointer;
  color: red;
  text-decoration: underline;
  display: inline-block;
}
</style>

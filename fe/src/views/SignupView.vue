<template>
  <div class="signup">
    <div class="head">회원가입</div>
    <form @submit.prevent="submitForm">
      <div class="input-body">
        <div class="input-head"><b>이메일</b></div>
        <div class="input-email">
          <input type="text" placeholder="EMAIL" v-model="email" />
        </div>
        <div class="alert-message" v-show="!isEmailValid && email !== ''">
          올바른 이메일 형식이 아닙니다.
        </div>
      </div>
      <div class="input-body">
        <div class="input-head"><b>비밀번호</b></div>
        <input type="password" placeholder="PASSWORD" v-model="password" />
        <div class="alert-message" v-show="!isPasswordValid && password !== ''">
          8, 20자 사이의 영어, 숫자, 특수문자를 사용하여 생성해주세요.
        </div>
      </div>
      <div class="input-body">
        <div class="input-head"><b>비밀번호 확인</b></div>
        <input type="password" placeholder="PASSWORD" v-model="passwordCheck" />
        <div class="alert-message" v-show="!isPasswordCheckValid && passwordCheck !== ''">
          비밀번호가 일치하지 않습니다.
        </div>
      </div>
      <div class="input-body">
        <div class="input-head"><b>이름</b></div>
        <input type="text" placeholder="NAME" v-model="name" />
        <div class="alert-message" v-show="!isNameValid && name !== ''">
          올바른 이름 형식이 아닙니다.
        </div>
      </div>
      <div class="input-body">
        <div class="input-head"><b>생년월일</b></div>
        <input type="date" placeholder="BIRTH" v-model="birthday" />
        <div class="alert-message" v-show="!isBirthdayValid">생년월일을 입력해주세요</div>
      </div>
      <div class="input-body">
        <div class="input-head"><b>전화번호</b></div>
        <input type="text" placeholder="CONTACT" v-model="contact" />
        <div class="alert-message" v-show="!isContactValid && contact !== ''">
          올바른 전화번호 형식이 아닙니다. 010-0000-0000
        </div>
      </div>
      <button :disabled="!isValidSignUp" type="submit" class="signup-button">회원가입</button>
    </form>
  </div>
</template>

<script>
import { registerMember } from "@/api/userApi";
import { validateEmail, validateContact, validateName, validatePassword } from "@/utils/validation"; // 유효성 검사
export default {
  name: "SignupView",
  data() {
    return {
      email: "",
      password: "",
      passwordCheck: "",
      name: "",
      birthday: "",
      contact: "",
    };
  },
  computed: {
    // 유효성 검사
    isEmailValid() {
      return validateEmail(this.email);
    },
    isPasswordValid() {
      return validatePassword(this.password);
    },
    isNameValid() {
      return validateName(this.name);
    },
    isContactValid() {
      return validateContact(this.contact);
    },
    isPasswordCheckValid() {
      return this.password === this.passwordCheck;
    },
    isBirthdayValid() {
      return !!this.birthday;
    },
    isValidSignUp() {
      if (
        this.email == "" ||
        this.password == "" ||
        this.passwordCheck == "" ||
        this.name == "" ||
        this.birthday == "" ||
        this.contact == ""
      )
        return false;
      return (
        this.isEmailValid &&
        this.isPasswordValid &&
        this.isNameValid &&
        this.isContactValid &&
        this.isPasswordCheckValid &&
        this.isBirthdayValid
      );
    },
  },
  methods: {
    async submitForm() {
      if (this.isValidSignUp) {
        const memberData = {
          email: this.email,
          password: this.password,
          passwordCheck: this.passwordCheck,
          name: this.name,
          birthday: this.birthday,
          contact: this.contact,
          isSocial: false,
        };
        try {
          await registerMember(memberData);
          alert(`${this.name}님이 가입되었습니다`);

          window.location.href = "/login";
        } catch (error) {
          alert("회원가입에 실패했습니다.");
          console.error(error.message);
        }
      }
    },
    initSignUp() {
      this.email = "";
      this.password = "";
      this.passwordCheck = "";
      this.name = "";
      this.birthday = "";
      this.contact = "";
    },
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

.checkbox-interest > input {
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
</style>

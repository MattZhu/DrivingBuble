package com.dt.android.serverapi;

import com.dt.android.serverapi.message.Question;
import com.dt.android.serverapi.message.QuestionResponse;
import com.dt.android.serverapi.message.RequestData;

public class SeedDataGenerator {

	
	public void generateQ(){
		
		RequestData datas = new RequestData();
		datas.set("api_name", "all_questions");
		datas.set("page_num", 0);
		datas.set("page_size", "100");
		QuestionResponse result;
		JsonApi<QuestionResponse> api = new JsonApi<QuestionResponse>(
				QuestionResponse.class);

		for (int i = 0; true; i++) {
			datas.set("page_num", i);
			result = api.post(datas);
			if (result == null
					|| result.getStatus_code() == 0
					|| result.getContent().length == 0) {
				break;
			}
			for(Question q:result.getContent()){
				toSql(q);
			}
		}
	}
	
	 private String toSql(Question q) {
			
			 return
			 "insert into QUESTION (question_id,is_a boolean,	is_b boolean,is_c boolean,"
			 +
			 " q_type integer, description TEXT,	picture TEXT,answer TEXT,answerDetail TEXT,"
			 +
			 " options TEXT )values("+q.getQuestion_id()+","+q.getIs_a()+","+q.getIs_b()+","+q.getIs_c()
			 +","+q.getType()+",'"+q.getDesc()+"','"+q.getPicture()+"','"+covertString(q.getCorrect_answer())+"','"+q.getExplanation()+"','"+covertString(q.getAnswer())+"');";
			 }
			 private String covertString(String[]
			 correct_answer) {
			 String result = "";
			 if (correct_answer == null ||
			 correct_answer.length == 0) {
			 return result;
			 }
			 for (String s : correct_answer) {
			 result += "==" + s;
			 }
			 return result.substring("==".length());
			 }
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SeedDataGenerator g=new SeedDataGenerator();
		g.generateQ();
	}

}

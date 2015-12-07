package com.dt.android.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.VideoView;

import com.dt.android.R;
import com.dt.android.domainobject.AppData;
import com.dt.android.domainobject.Question;
import com.dt.android.utils.ImageLoadTask;
import com.dt.android.utils.VideoLoadTask;

public class QuestionItemViewCreator implements ListItemViewCreator<Question> {

	private Context context;

	private OnCheckedChangeListener onCheckedChangeListener;

	private boolean checkAnswer;

	private boolean testing = false;

	private LruCache<String, Bitmap> imageCache = new LruCache<String, Bitmap>(
			4 * 1024 * 1024);

	private LruCache<Integer, View> viewCache = new LruCache<Integer, View>(
			2 * 1024 * 1024);

	private OnClickListener submitAnswer;

	public QuestionItemViewCreator(Context context,
			OnCheckedChangeListener onCheckedChangeListener,
			OnClickListener submitAnswer) {
		this.context = context;
		this.onCheckedChangeListener = onCheckedChangeListener;
		this.submitAnswer = submitAnswer;
	}

	public View createOrUpdateView(Question item, View convertView,
			int position, ViewGroup parent) {
		Log.d("QuestionItemViewCreator", "Question " + item + "");
		View result = null;
		RadioGroup radioGroup = null;
		LinearLayout mOptions = null;
		if (convertView == null) {
			Log.d("QuestionItemViewCreator", "inflate view");
			result = viewCache.get(position);
			if (result == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				result = inflater.inflate(R.layout.questionitem, null);
				radioGroup = (RadioGroup) result.findViewById(R.id.options);
				mOptions = (LinearLayout) result.findViewById(R.id.optionsM);
				createQuestionOptions(item, result, radioGroup, mOptions);
				radioGroup.setOnCheckedChangeListener(onCheckedChangeListener);

				if (item.getImage() != null && item.getImage().length() > 0) {
					updateImageOrVideo(item, result,position);
				}
				viewCache.put(position, result);
			} else {
				radioGroup = (RadioGroup) result.findViewById(R.id.options);
				mOptions = (LinearLayout) result.findViewById(R.id.optionsM);
			}
		} else {
			Log.d("QuestionItemViewCreator", "reuse view");
			result = convertView;
			radioGroup = (RadioGroup) result.findViewById(R.id.options);
			mOptions = (LinearLayout) result.findViewById(R.id.optionsM);
		}
		TextView quesitonDesc = (TextView) result
				.findViewById(R.id.questionDesc);
		quesitonDesc.setText(item.getDesciption());
		LayoutParams params = quesitonDesc.getLayoutParams();
		params.width = context.getResources().getDisplayMetrics().widthPixels
				- (int) (28 * context.getResources().getDisplayMetrics().density);
		quesitonDesc.setLayoutParams(params);

		if (!testing) {
			showAnswer(result, item, item.isAnswered() || checkAnswer);
		} else {
			if (item.isSingleOption()) {
				showUserSelected(radioGroup, item);
			} else {
				showUserSelected(mOptions, item);
			}
		}

		Log.d("QuestionItemViewCreator", "finishe create or update view");
		return result;

	}

	private void updateImageOrVideo(Question item, View result,int position) {
		if (isImage(item.getImage())) {
			ImageView iV = (ImageView) result.findViewById(R.id.questionImage);
			iV.setVisibility(View.VISIBLE);
			Bitmap image = imageCache.get(item.getImage());
			if (image != null) {
				iV.setImageBitmap(image);
			} else {
				ImageLoadTask task = new ImageLoadTask(iV, context);
				task.setImageCache(imageCache);
				task.execute(item.getImage(),
						AppData.QUESTION_IMAGE_CACHE_FOLDER, item.getId()
								+ ".png");
			}
		} else if (isVedio(item.getImage())) {
			ImageView iV = (ImageView) result.findViewById(R.id.questionImage);
			iV.setVisibility(View.VISIBLE);
			VideoView videoView = (VideoView) result
					.findViewById(R.id.questionVedio);
			VideoLoadTask task = new VideoLoadTask(videoView, iV, context);
			task.setPlay(position==0);
			task.execute(item.getImage(), AppData.QUESTION_VEDIO_CACHE_FOLDER,
					item.getId() + ".mp4");
			
		} else {
			Log.d("QuestionItemView",
					"Unsupported image file:" + item.getImage());
		}
	}

	private void createQuestionOptions(Question item, View result,
			RadioGroup radioGroup, LinearLayout mOptions) {
		if (item.isSingleOption()) {
			int id = 0;
			for (String option : item.getOptions()) {
				RadioButton radioButton = new RadioButton(context);
				radioButton.setId(id);
				radioButton.setText(option);
				radioButton.setTextColor(Color.rgb(0x66, 0x66, 0x66));
				radioButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
				radioGroup.addView(radioButton);
				id++;
			}
		} else {
			radioGroup.setVisibility(View.GONE);

			mOptions.setVisibility(View.VISIBLE);
			int id = 0;
			for (String option : item.getOptions()) {

				CheckBox checkBox = new CheckBox(context);
				checkBox.setId(id);
				checkBox.setText(option);
				checkBox.setTextColor(Color.rgb(0x66, 0x66, 0x66));
				checkBox.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
				mOptions.addView(checkBox);
				id++;
			}
			Button btn = (Button) result.findViewById(R.id.answerBtn);
			if (!item.isAnswered() && !this.checkAnswer) {
				btn.setVisibility(View.VISIBLE);
				btn.setOnClickListener(submitAnswer);
			} else {
				btn.setVisibility(View.GONE);
			}
		}
	}

	private boolean isVedio(String image) {
		return image.endsWith(".mp4");
	}

	private boolean isImage(String image) {
		return image.endsWith(".jpg") || image.endsWith(".gif");
	}

	public void showUserSelected(LinearLayout group, Question item) {
		if (item.isAnswered()) {
			enableGroup(group, false);
			for (Integer i : item.getUserAnswer()) {
				CompoundButton b = (CompoundButton) group.getChildAt(i);
				b.setChecked(true);
			}
		}
	}

	public void showAnswer(View qestion, Question question, boolean enabled) {
		Log.d("QustionActivity",
				"Question is isSingleOption:" + question.isSingleOption());
		if (question.isSingleOption()) {
			RadioGroup radioGroup = (RadioGroup) qestion
					.findViewById(R.id.options);
			showAnswerRadio(radioGroup, question, enabled);
		} else {
			LinearLayout options = (LinearLayout) qestion
					.findViewById(R.id.optionsM);
			showAnswerCheckBox(options, question, enabled);
			Button btn = (Button) qestion.findViewById(R.id.answerBtn);
			if(btn!=null){
				if (!enabled && !question.isAnswered()) {
					btn.setVisibility(View.VISIBLE);
					btn.setOnClickListener(submitAnswer);
				} else {
					btn.setVisibility(View.GONE);
				}
			}
		}
		showExplaination(qestion, question, enabled);

	}

	private void showExplaination(View qestion, Question question,
			boolean enabled) {
		TextView exp = (TextView) qestion.findViewById(R.id.explaination);
		if (question.isAnswered()) {
			if (!question.isCorrected()) {
				exp.setVisibility(View.VISIBLE);
				exp.setText(question.getAnswerDetail());
			}
		} else {
			if (enabled) {
				exp.setVisibility(View.VISIBLE);
				exp.setText(question.getAnswerDetail());
			} else {
				exp.setVisibility(View.GONE);
			}
		}

	}

	private void showAnswerCheckBox(LinearLayout options, Question question,
			boolean enabled) {
		if (question.isAnswered()) {
			enableGroup(options, false);
			CheckBox cb = null;
			if (question.isCorrected()) {

				for (Integer i : question.getAnswer()) {
					cb = (CheckBox) options.getChildAt(i);
//					cb.setButtonDrawable(R.drawable.correct);
					cb.setTextColor(Color.rgb(136, 201, 48));
				}

			} else {
				for(int i=0;i<question.getOptions().size();i++){
					cb = (CheckBox) options.getChildAt(i);
					boolean userslected=false;
					boolean correctansweroption=false;
					for (Integer userAnswer : question.getUserAnswer()) {
						if(userAnswer.equals(i)){
							userslected=true;
							break;
						}
					}
					for (Integer ca : question.getAnswer()) {
						if (ca.equals(i)) {
							correctansweroption = true;
							break;
						}
					}
					if (userslected) {
						cb.setChecked(true);
						if(correctansweroption){
							cb.setTextColor(Color.rgb(136, 201, 48));
						}else{
							cb.setTextColor(Color.rgb(196, 4, 4));
						}
					} else {
						cb.setChecked(false);
						if(correctansweroption )
						cb.setTextColor(Color.rgb(196, 4, 4));
						else
							cb.setTextColor(Color.rgb(136, 201, 48));
					}
				}
				
			}
		} else {
			enableGroup(options, !enabled);
			if (enabled) {
				for (Integer userAnswer : question.getAnswer()) {
					CheckBox cb = (CheckBox) options.getChildAt(userAnswer);
					if (cb != null) {
//						cb.setButtonDrawable(R.drawable.correct);
						cb.setChecked(true);
						cb.setTextColor(Color.rgb(136, 201, 48));
					} else {
						Log.d("QuestionItem", "invalid questions answer:"
								+ Arrays.toString(question.getAnswer()));
					}
				}
			} else {
				options.removeAllViews();
				int id = 0;
				for (String option : question.getOptions()) {

					CheckBox checkBox = new CheckBox(context);
					checkBox.setId(id);
					checkBox.setText(option);
					checkBox.setTextColor(Color.rgb(0x66, 0x66, 0x66));
					checkBox.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
					checkBox.setEnabled(!enabled);
					options.addView(checkBox);
					id++;
				}
			}
		}
	}

	private void showAnswerRadio(RadioGroup group, Question question,
			boolean enabled) {

		if (question.isAnswered()) {
			enableGroup(group, false);
			RadioButton rb = (RadioButton) group.getChildAt(question
					.getUserAnswer()[0]);
			if (question.isCorrected()) {

				rb.setButtonDrawable(R.drawable.correct);
				rb.setTextColor(Color.rgb(136, 201, 48));
			} else {
				rb.setButtonDrawable(R.drawable.error);
				rb.setTextColor(Color.rgb(196, 4, 4));
				rb = (RadioButton) group.getChildAt(question.getAnswer()[0]);
				if (rb != null) {
					rb.setButtonDrawable(R.drawable.correct);
					rb.setTextColor(Color.rgb(136, 201, 48));
				} else {
					Log.d("QuestionItem",
							"invalid questions answer:"
									+ Arrays.toString(question.getAnswer()));
				}
			}
		} else {
			enableGroup(group, !enabled);
			RadioButton rb = (RadioButton) group.getChildAt(question
					.getAnswer()[0]);
			if (rb != null) {
				if (enabled) {

					rb.setButtonDrawable(R.drawable.correct);
					rb.setTextColor(Color.rgb(136, 201, 48));
				} else {

					RadioButton radioButton = new RadioButton(context);
					radioButton.setId(question.getAnswer()[0]);
					radioButton.setText(question.getOptions().get(
							question.getAnswer()[0]));
					radioButton.setTextColor(Color.rgb(0x66, 0x66, 0x66));
					radioButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
					group.removeViewAt(question.getAnswer()[0]);
					group.addView(radioButton, question.getAnswer()[0]);
				}
			} else {
				Log.d("QuestionItem",
						"invalid questions answer:"
								+ Arrays.toString(question.getAnswer()));
			}
		}
	}

	private void enableGroup(LinearLayout group, boolean enabled) {
		for (int i = 0; i < group.getChildCount(); i++) {
			(group.getChildAt(i)).setEnabled(enabled);
		}
		group.setEnabled(enabled);
	}

	/**
	 * @return the checkAnswer
	 */
	public boolean isCheckAnswer() {
		return checkAnswer;
	}

	/**
	 * @param checkAnswer
	 *            the checkAnswer to set
	 */
	public void setCheckAnswer(boolean checkAnswer) {
		this.checkAnswer = checkAnswer;
	}

	public OnClickListener getSubmitAnswer() {
		return submitAnswer;
	}

	public void setSubmitAnswer(OnClickListener submitAnswer) {
		this.submitAnswer = submitAnswer;
	}

	/**
	 * @return the testing
	 */
	public boolean isTesting() {
		return testing;
	}

	/**
	 * @param testing
	 *            the testing to set
	 */
	public void setTesting(boolean testing) {
		this.testing = testing;
	}

}

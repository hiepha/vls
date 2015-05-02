package com.vlls.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlls.exception.InvalidRequestItemException;
import com.vlls.exception.NoInstanceException;
import com.vlls.exception.ServerTechnicalException;
import com.vlls.exception.UnauthenticatedException;
import com.vlls.exception.UnauthorizedException;
import com.vlls.jpa.domain.Item;
import com.vlls.jpa.domain.LearningItem;
import com.vlls.jpa.domain.Question;
import com.vlls.jpa.domain.QuestionAnswerType;
import com.vlls.jpa.domain.QuestionIncorrectAnswerType;
import com.vlls.jpa.domain.QuestionType;
import com.vlls.jpa.domain.User;
import com.vlls.jpa.repository.QuestionRepository;
import com.vlls.web.model.QuestionPageResponse;
import com.vlls.web.model.QuestionResponse;
import com.vlls.web.model.QuizQuestionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by hiephn on 2014/10/10.
 */
@Service
public class QuestionService extends AbstractService {

    private static final String DEFAULT_INPUT_NAME_TEMPLATE = "'${meaning}'?";
    private static final Integer DEFAULT_INCORRECT_ANSWER_NUM = 3;

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private InterMessageService interMessageService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private Random random = new Random();

    public QuestionPageResponse get(Integer itemId, Integer id)
            throws NoInstanceException, UnauthenticatedException, UnauthorizedException {
        if (id > 0) {
            Question question = this.get0(id);
            QuestionPageResponse questionPageResponse = new QuestionPageResponse();
            questionPageResponse.from(question);
            return questionPageResponse;
        } else {
            List<Question> questions = this.getList0(itemId);
            QuestionPageResponse questionPageResponse = new QuestionPageResponse();
            questionPageResponse.from(questions);
            return questionPageResponse;
        }
    }

    @Transactional
    public QuestionResponse create(String text, Integer typeIndex, String answerTypeString,
                                   Integer incorrectAnswerTypeIndex,
                                   Integer incorrectAnswerNum, String incorrectAnswerRaw,
                                   Integer correctAnswerItemId, Integer[] incorrectAnswerItemIds,
                                   Boolean isSystemGenerated)
            throws NoInstanceException, InvalidRequestItemException {
        Question question = this.create0(text, typeIndex, answerTypeString, incorrectAnswerTypeIndex,
                incorrectAnswerNum, incorrectAnswerRaw, correctAnswerItemId, incorrectAnswerItemIds, isSystemGenerated);
        QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.setData(question);
        return questionResponse;
    }

    @Transactional
    public QuestionResponse update(Integer id, String text, Integer typeIndex, String answerTypeString,
                                   Integer incorrectAnswerTypeIndex,
                                   Integer incorrectAnswerNum, String incorrectAnswerRaw,
                                   Integer correctAnswerItemId, Integer[] incorrectAnswerItemIds,
                                   Boolean isSystemGenerated)
            throws NoInstanceException, InvalidRequestItemException, UnauthenticatedException, UnauthorizedException {
        Question question = this.update0(id, text, typeIndex, answerTypeString, incorrectAnswerTypeIndex,
                incorrectAnswerNum, incorrectAnswerRaw, correctAnswerItemId, incorrectAnswerItemIds, isSystemGenerated);
        QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.setData(question);
        return questionResponse;
    }

    Question get0(Integer id) throws NoInstanceException, UnauthenticatedException, UnauthorizedException {
        // Permit only creator to retrieve the question
        User user = userService.getCreatorByQuestion0(id);
        securityService.permitUser(user);

        Question question = questionRepository.findOne(id);
        if (question == null) {
            throw new NoInstanceException("Question", id);
        } else {
            return question;
        }
    }

    List<Question> getList0(Integer itemId)
            throws NoInstanceException, UnauthenticatedException, UnauthorizedException {
        // Permit only creator to retrieve the question list
        User creator = userService.getCreatorByItem0(itemId);
        securityService.permitUser(creator);

        List<Question> questions = questionRepository.findByCorrectAnswerItemId(itemId);
        return questions;
    }

    @Transactional
    void createDefaultQuestions0(Item item) {
        this.createDefaultMeaningQuestion0(item);
        this.createDefaultNameQuestion0(item);
        this.createDefaultInputNameQuestion0(item);
        // Don't use this kind of question
        //this.createDefaultInputMeaningQuestion0(item);
    }

    @Transactional
    void updateDefaultQuestions0(Item item) {
        String localeCode = item.getLevel().getCourse().getLangSpeak().getCode();
        final String DEFAULT_QUESTION_TEMPLATE = interMessageService.
                getMessage(localeCode, "default-question-template");
        item.getQuestions().forEach(question -> {
            if (question.getIsSystemGenerated()) {
                if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
                    if (question.getAnswerType() == QuestionAnswerType.MEANING) {
                        String textFormat = this.getDefaultMeaningQuestionTextFormat(localeCode);
                        String text = this.replaceQuestionPlaceHolder(textFormat, item);
                        question.setText(text);
                    } else { // QuestionAnswerType.WORD
                        String textFormat = this.getDefaultNameQuestionTextFormat(localeCode);
                        String text = this.replaceQuestionPlaceHolder(textFormat, item);
                        question.setText(text);
                    }
                } else { // QuestionType.USER_INPUT
                    if (question.getAnswerType() == QuestionAnswerType.MEANING) {
                        // Don't use this type of question
                    } else { // QuestionAnswerType.WORD
                        String text = this.replaceQuestionPlaceHolder(DEFAULT_INPUT_NAME_TEMPLATE, item);
                        question.setText(text);
                    }
                }
            }
        });
    }

    private String replaceQuestionPlaceHolder(String textFormat, Item item) {
        return replace(textFormat, "${name}", item.getName()).
                replace("${meaning}", item.getMeaning());
    }

    private String getDefaultQuestionTemplate(String locale) {
        return interMessageService.getMessage(locale, "default-question-template");
    }

    private String getDefaultMeaningQuestionTextFormat(String locale) {
        String defaultQuestionTemplate = this.getDefaultQuestionTemplate(locale);
        return String.format(defaultQuestionTemplate, "${name}");
    }

    private Question createDefaultMeaningQuestion0(Item item) {
        String localeCode = item.getLevel().getCourse().getLangSpeak().getCode();
        String textFormat = this.getDefaultMeaningQuestionTextFormat(localeCode);
        String text = this.replaceQuestionPlaceHolder(textFormat, item);
        return this.create0(
                text,
                QuestionType.MULTIPLE_CHOICE,
                QuestionAnswerType.MEANING,
                QuestionIncorrectAnswerType.REST_OF_COURSE,
                DEFAULT_INCORRECT_ANSWER_NUM,
                null,
                item,
//                null,
                true
        );
    }

    private String getDefaultNameQuestionTextFormat(String locale) {
        String defaultQuestionTemplate = this.getDefaultQuestionTemplate(locale);
        return String.format(defaultQuestionTemplate, "${meaning}");
    }

    private Question createDefaultNameQuestion0(Item item) {
        String localeCode = item.getLevel().getCourse().getLangSpeak().getCode();
        String textFormat = this.getDefaultNameQuestionTextFormat(localeCode);
        String text = this.replaceQuestionPlaceHolder(textFormat, item);
        return this.create0(
                text,
                QuestionType.MULTIPLE_CHOICE,
                QuestionAnswerType.WORD,
                QuestionIncorrectAnswerType.REST_OF_COURSE,
                DEFAULT_INCORRECT_ANSWER_NUM,
                null,
                item,
//                null,
                true
        );
    }

//    private Question createDefaultInputMeaningQuestion0(Item item) {
//        return this.create0(
//                String.format(DEFAULT_INPUT_NAME_TEMPLATE, item.getName()),
//                QuestionType.USER_INPUT,
//                QuestionAnswerType.MEANING,
//                null,
//                null,
//                null,
//                item,
//                null,
//                true
//        );
//    }

    private Question createDefaultInputNameQuestion0(Item item) {
        String text = this.replaceQuestionPlaceHolder(DEFAULT_INPUT_NAME_TEMPLATE, item);
        return this.create0(
                text,
                QuestionType.USER_INPUT,
                QuestionAnswerType.WORD,
                null,
                null,
                null,
                item,
//                null,
                true
        );
    }

    /**
     * <p>
     * Question types: Multiple choice, Input<br/>
     * Answer types: Word, Meaning<br/>
     * Incorrect answer types: Rest of course/level items, Some items from course/level, Input manually
     * </p>
     *
     * Mandatory params:
     *   text, type, answerType, correctAnswerItem
     *
     * Custom params for each case:
     * 1/ Q:Inp, A:Wrd/Me:
     *   N/A
     * 2/ Q:Mul, A:Wrd/Me, IA: ResCor/ResLev:
     *   incorrectAnswerType, incorrectAnswerNum
     * 3/ Q:Mul, A:Wrd/Me, IA: SmCor/SmLev:
     *   incorrectAnswerType, incorrectAnswerNum, incorrectAnswerItems
     * 4/ Q:Mul, A:Wrd/Me, IA: Inp:
     *   incorrectAnswerType, incorrectAnswerNum, incorrectAnswerRaw
     *
     * @param text text
     * @param typeIndex typeString
     * @param answerTypeString answerTypeString
     * @param incorrectAnswerTypeIndex incorrectAnswerTypeString
     * @param incorrectAnswerNum incorrectAnswerNum
     * @param incorrectAnswerRaw incorrectAnswerRaw
     * @param correctAnswerItemId correctAnswerItemId
     * @param incorrectAnswerItemIds incorrectAnswerItemIds
     * @return created Question
     * @throws InvalidRequestItemException
     * @throws NoInstanceException
     */
    @Transactional
    Question create0(String text, Integer typeIndex, String answerTypeString,
                     Integer incorrectAnswerTypeIndex,
                     Integer incorrectAnswerNum, String incorrectAnswerRaw,
                     Integer correctAnswerItemId, Integer[] incorrectAnswerItemIds,
                     Boolean isSystemGenerated)
            throws InvalidRequestItemException, NoInstanceException {

        /***** MANDATORY PARAMS *****/
        // Get question type
        QuestionType type = QuestionType.fromIndex(typeIndex);
        // Get answer type
        QuestionAnswerType answerType = QuestionAnswerType.fromDisplay(answerTypeString);
        // Get correct item
        Item correctAnswerItem = itemService.get0(correctAnswerItemId);
        /***** MANDATORY PARAMS ends *****/

        /***** CUSTOM PARAMS *****/
        if (type == QuestionType.USER_INPUT) {
            /***** CASE 1 *****/
            return this.create0(text, type, answerType, null, null, null, correctAnswerItem,
//                    null,
                    isSystemGenerated);
            /***** CASE 1 ends *****/
        } else { // Case 2, 3, 4
            // Get incorrect answer type
            QuestionIncorrectAnswerType incorrectAnswerType = QuestionIncorrectAnswerType.
                    fromIndex(incorrectAnswerTypeIndex);
            if (incorrectAnswerType == QuestionIncorrectAnswerType.REST_OF_COURSE
                    || incorrectAnswerType == QuestionIncorrectAnswerType.REST_OF_LEVEL) {
                /***** CASE 2 *****/
                return this.create0(text, type, answerType, incorrectAnswerType, incorrectAnswerNum, null,
                        correctAnswerItem,
//                        null,
                        isSystemGenerated);
                /***** CASE 2 ends *****/
//            } else if (incorrectAnswerType == QuestionIncorrectAnswerType.SOME_FROM_COURSE
//                    || incorrectAnswerType == QuestionIncorrectAnswerType.SOME_FROM_LEVEL) {
//                /***** CASE 3 *****/
//                // Get incorrect item list
//                List<Item> incorrectAnswerItems = itemService.getList0(incorrectAnswerItemIds);
//                return this.create0(text, type, answerType, incorrectAnswerType, incorrectAnswerNum, null,
//                        correctAnswerItem, incorrectAnswerItems, isSystemGenerated);
//                /***** CASE 3 ends *****/
            } else {
                /***** CASE 4 *****/
                return this.create0(text, type, answerType, incorrectAnswerType, incorrectAnswerNum, incorrectAnswerRaw,
                        correctAnswerItem,
//                        null,
                        isSystemGenerated);
                /***** CASE 4 ends *****/
            }
        }
        /***** CUSTOM PARAMS ends *****/
    }

    /**
     * See {@link #create0(String, Integer, String, Integer, Integer, String, Integer, Integer[], Boolean)}
     * @param text
     * @param type
     * @param answerType
     * @param incorrectAnswerType
     * @param incorrectAnswerNum
     * @param incorrectAnswerRaw
     * @param correctAnswerItem
     * @param incorrectAnswerItems
     * @return
     */
    @Transactional
    Question create0(String text, QuestionType type, QuestionAnswerType answerType,
                     QuestionIncorrectAnswerType incorrectAnswerType,
                     Integer incorrectAnswerNum, String incorrectAnswerRaw,
                     Item correctAnswerItem,
//                     List<Item> incorrectAnswerItems,
                     Boolean isSystemGenerated) {
        Question question = new Question();
        question.setText(text);
        question.setType(type);
        question.setAnswerType(answerType);
        question.setIncorrectAnswerType(incorrectAnswerType);
        question.setIncorrectAnswerNum(incorrectAnswerNum);
        question.setIncorrectAnswerRaw(incorrectAnswerRaw);
        question.setCorrectAnswerItem(correctAnswerItem);
//        question.setIncorrectAnswerItems(incorrectAnswerItems);
        question.setIsSystemGenerated(isSystemGenerated);
        questionRepository.save(question);
        if (correctAnswerItem.getQuestions() == null) {
            correctAnswerItem.setQuestions(new ArrayList<>());
        }
        correctAnswerItem.getQuestions().add(question);
        return question;
    }

    /**
     * Similar to {@link #create0(String, Integer, String, Integer, Integer, String, Integer, Integer[], Boolean)}
     * @param id
     * @param text
     * @param typeIndex
     * @param answerTypeString
     * @param incorrectAnswerTypeIndex
     * @param incorrectAnswerNum
     * @param incorrectAnswerRaw
     * @param correctAnswerItemId
     * @param incorrectAnswerItemIds
     * @return
     */
    @Transactional
    Question update0(Integer id, String text, Integer typeIndex, String answerTypeString,
                     Integer incorrectAnswerTypeIndex,
                     Integer incorrectAnswerNum, String incorrectAnswerRaw,
                     Integer correctAnswerItemId, Integer[] incorrectAnswerItemIds,
                     Boolean isSystemGenerated)
            throws InvalidRequestItemException, NoInstanceException, UnauthenticatedException, UnauthorizedException {

        /***** MANDATORY PARAMS *****/
        // Get question type
        QuestionType type = QuestionType.fromIndex(typeIndex);
        // Get answer type
        QuestionAnswerType answerType = QuestionAnswerType.fromDisplay(answerTypeString);
        // Get correct item
        Item correctAnswerItem = itemService.get0(correctAnswerItemId);
        /***** MANDATORY PARAMS ends *****/

        /***** CUSTOM PARAMS *****/
        if (type == QuestionType.USER_INPUT) {
            /***** CASE 1 *****/
            return this.update0(id, text, type, answerType, null, null, null, correctAnswerItem,
//                    null,
                    isSystemGenerated);
            /***** CASE 1 ends *****/
        } else { // Case 2, 3, 4
            // Get incorrect answer type
            QuestionIncorrectAnswerType incorrectAnswerType = QuestionIncorrectAnswerType.
                    fromIndex(incorrectAnswerTypeIndex);
            if (incorrectAnswerType == QuestionIncorrectAnswerType.REST_OF_COURSE
                    || incorrectAnswerType == QuestionIncorrectAnswerType.REST_OF_LEVEL) {
                /***** CASE 2 *****/
                return this.update0(id, text, type, answerType, incorrectAnswerType, incorrectAnswerNum, null,
                        correctAnswerItem,
//                        null,
                        isSystemGenerated);
                /***** CASE 2 ends *****/
//            } else if (incorrectAnswerType == QuestionIncorrectAnswerType.SOME_FROM_COURSE
//                    || incorrectAnswerType == QuestionIncorrectAnswerType.SOME_FROM_LEVEL) {
//                /***** CASE 3 *****/
//                // Get incorrect item list
//                List<Item> incorrectAnswerItems = itemService.getList0(incorrectAnswerItemIds);
//                return this.update0(id, text, type, answerType, incorrectAnswerType, incorrectAnswerNum, null,
//                        correctAnswerItem, incorrectAnswerItems, isSystemGenerated);
//                /***** CASE 3 ends *****/
            } else {
                /***** CASE 4 *****/
                return this.update0(id, text, type, answerType, incorrectAnswerType, incorrectAnswerNum,
                        incorrectAnswerRaw, correctAnswerItem,
//                        null,
                        isSystemGenerated);
                /***** CASE 4 ends *****/
            }
        }
        /***** CUSTOM PARAMS ends *****/
    }

    @Transactional
    Question update0(Integer id, String text, QuestionType type, QuestionAnswerType answerType,
                     QuestionIncorrectAnswerType incorrectAnswerType,
                     Integer incorrectAnswerNum, String incorrectAnswerRaw,
                     Item correctAnswerItem,
//                     List<Item> incorrectAnswerItems,
                     Boolean isSystemGenerated)
            throws NoInstanceException, UnauthenticatedException, UnauthorizedException {
        Question question = this.get0(id);
        return this.update0(question, text, type, answerType, incorrectAnswerType, incorrectAnswerNum,
                incorrectAnswerRaw, correctAnswerItem,
//                incorrectAnswerItems,
                isSystemGenerated);
    }

    @Transactional
    Question update0(Question question, String text, QuestionType type, QuestionAnswerType answerType,
                    QuestionIncorrectAnswerType incorrectAnswerType,
                    Integer incorrectAnswerNum, String incorrectAnswerRaw,
                    Item correctAnswerItem,
//                    List<Item> incorrectAnswerItems,
                    Boolean isSystemGenerated) {
        question.setText(text);
        question.setType(type);
        question.setAnswerType(answerType);
        question.setIncorrectAnswerType(incorrectAnswerType);
        question.setIncorrectAnswerNum(incorrectAnswerNum);
        question.setIncorrectAnswerRaw(incorrectAnswerRaw);
        question.setCorrectAnswerItem(correctAnswerItem);
//        question.setIncorrectAnswerItems(incorrectAnswerItems);
        question.setIsSystemGenerated(isSystemGenerated);
        return question;
    }

    public void delete(Integer id)
            throws NoInstanceException, UnauthenticatedException, UnauthorizedException {
        User creator = userService.getCreatorByQuestion0(id);
        securityService.permitUser(creator);
        questionRepository.delete(id);
    }

    List<QuizQuestionResponse> toQuizList0(LearningItem learningItem, Integer courseId, Integer levelId)
            throws ServerTechnicalException {
        List<Question> fullQuestions = learningItem.getItem().getQuestions();
        List<Question> questions = new ArrayList<>();
        if (fullQuestions.size() > 0) {
            Integer curRightNo = (learningItem.getRightNo() == null ? 0 : learningItem.getRightNo());
            final int QUESTION_NUM = Math.max(
                    LearningItemService.RIGHT_NO_TO_FINISH - curRightNo,
                    0);
            int fullQuestionsIndex = 0;
            for (int i = 0; i < QUESTION_NUM; ++i) {
                questions.add(fullQuestions.get(fullQuestionsIndex++));
                if (fullQuestionsIndex == fullQuestions.size()) {
                    fullQuestionsIndex = 0;
                }
            }
        }

        List<QuizQuestionResponse> quizQuestionResponses = new ArrayList<>(questions.size());
        for (Question question : questions) {
            QuizQuestionResponse quizQuestionResponse = this.toQuiz0(question, courseId, levelId);
            quizQuestionResponses.add(quizQuestionResponse);
        }

        return quizQuestionResponses;
    }

    List<QuizQuestionResponse> toReviseQuizList0(LearningItem learningItem, Integer courseId, Integer levelId)
            throws ServerTechnicalException {
        List<Question> fullQuestions = learningItem.getItem().getQuestions();
        Question question;
        if (fullQuestions.size() > 0) {
            question = fullQuestions.get(random.nextInt(fullQuestions.size()));
            List<QuizQuestionResponse> quizQuestionResponses = new ArrayList<>(1);
            QuizQuestionResponse quizQuestionResponse = this.toQuiz0(question, courseId, levelId);
            quizQuestionResponses.add(quizQuestionResponse);
            return quizQuestionResponses;
        } else {
            return new ArrayList<>(0);
        }
    }

    /**
     * See explanation of 4 cases here: {@link #create0(String, Integer, String, Integer, Integer, String, Integer, Integer[], Boolean)}
     *
     * @param courseId course id of item of question
     * @param question question
     * @return quiz formatted question
     */
    QuizQuestionResponse toQuiz0(Question question, Integer courseId, Integer levelId)
            throws ServerTechnicalException {

        QuizQuestionResponse quizQuestionResponse = new QuizQuestionResponse();
        /***** COMMON INFORMATION (text, type, correct answer) *****/
        quizQuestionResponse.setText(question.getText());
        quizQuestionResponse.setType(question.getType().getIndex());
        if (question.getAnswerType() == QuestionAnswerType.WORD) {
            quizQuestionResponse.setAnswer(question.getCorrectAnswerItem().getName());
        } else {
            quizQuestionResponse.setAnswer(question.getCorrectAnswerItem().getMeaning());
        }
        /***** COMMON INFORMATION ends *****/

        if (question.getType() == QuestionType.USER_INPUT) {
            /***** CASE 1 *****/
            // Nothing to do
            /***** CASE 1 ends *****/
        } else  { // CASE 2, 3, 4
            List<Integer> selectedItemIds = new ArrayList<>();
            List<Integer> incorrectItemIds;
            if (question.getIncorrectAnswerType() == QuestionIncorrectAnswerType.INPUT_MANUALLY) {
                /***** CASE 4 *****/
                try {
                    List fullIncorrectAnswers = objectMapper.readValue(question.getIncorrectAnswerRaw(), List.class);
                    List<String> incorrectAnswers = new ArrayList<>();
                    for (int i = 0; i < question.getIncorrectAnswerNum(); ++i) {
                        if (fullIncorrectAnswers.size() > 0) {
                            String incorrectAnswer = String.valueOf(fullIncorrectAnswers.get(random.nextInt(fullIncorrectAnswers.size())));
                            incorrectAnswers.add(incorrectAnswer);
                            fullIncorrectAnswers.remove(incorrectAnswer);
                        }
                    }
                    quizQuestionResponse.setOptions(incorrectAnswers);
                } catch (IOException e) {
                    throw new ServerTechnicalException("Error while generating question");
                }
                /***** CASE 4 ends *****/
            } else { // CASE 2, 3
                if (question.getIncorrectAnswerType() == QuestionIncorrectAnswerType.REST_OF_COURSE) {
                    /***** CASE 2 *****/
                    /***** CASE 2.1 *****/
                    // Get incorrect ids
                    incorrectItemIds = itemService.getItemIdsByCourse0(courseId);
                    /***** CASE 2.1 ends *****/
                } else {
//                    if (question.getIncorrectAnswerType() == QuestionIncorrectAnswerType.REST_OF_LEVEL) {
                    /***** CASE 2.2 *****/
                    // Get incorrect ids
                    incorrectItemIds = itemService.getItemIdsByLevel0(levelId);
                    /***** CASE 2.2 ends *****/
                    /***** CASE 2 ends *****/
                }
//                else {
//                    /***** CASE 3 *****/
//                    // Get incorrect ids
//                    incorrectItemIds = itemService.getIncorrectAnswerIdsByQuestion0(question.getId());
//                    /***** CASE 3 ends *****/
//                }
                // Remove correct answer id if contained
                incorrectItemIds.remove(question.getCorrectAnswerItem().getId());

                // Remove incorrect items that has the name equals to correct item
                // if incorrectItem.name = correctItem.name but their meaning is different, it is dangerous
                if (incorrectItemIds.size() > 0) {
                    incorrectItemIds = itemService.removeFromListIfNameEqual(
                            incorrectItemIds, question.getCorrectAnswerItem().getName());
                }

                // Get random ids
                for (int i = 0; i < question.getIncorrectAnswerNum(); ++i) {
                    if (incorrectItemIds.size() > 0) {
                        Integer randomizedItemId = incorrectItemIds.get(random.nextInt(incorrectItemIds.size()));
                        selectedItemIds.add(randomizedItemId);
                        incorrectItemIds.remove(randomizedItemId);
                    }
                }
                // Get answer string
                if (selectedItemIds.size() > 0) {
                    if (question.getAnswerType() == QuestionAnswerType.WORD) {
                        List<String> itemsNames = itemService.getItemsNamesByIds0(selectedItemIds);
                        quizQuestionResponse.setOptions(itemsNames);
                    } else { // MEANING
                        List<String> itemsMeanings = itemService.getItemsMeaningsByIds0(selectedItemIds);
                        quizQuestionResponse.setOptions(itemsMeanings);
                    }
                } else {
                    quizQuestionResponse.setOptions(new ArrayList<>());
                }
            }
            /***** CASE 2 ends *****/

            // Inject correct answer into options
            if (quizQuestionResponse.getOptions().size() > 0) {
                String option = quizQuestionResponse.getOptions().set(
                        random.nextInt(quizQuestionResponse.getOptions().size()),
                        quizQuestionResponse.getAnswer());
                quizQuestionResponse.getOptions().add(option);
            } else {
                quizQuestionResponse.getOptions().add(quizQuestionResponse.getAnswer());
            }
        }

        return quizQuestionResponse;
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        String out = list.set(new Random().nextInt(list.size()), "a String");
        list.add(out);
    }
}

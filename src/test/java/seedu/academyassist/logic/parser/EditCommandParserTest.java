package seedu.academyassist.logic.parser;

import static seedu.academyassist.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.academyassist.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.academyassist.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.academyassist.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.academyassist.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.academyassist.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static seedu.academyassist.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static seedu.academyassist.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static seedu.academyassist.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static seedu.academyassist.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.academyassist.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.academyassist.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.academyassist.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.academyassist.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static seedu.academyassist.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static seedu.academyassist.logic.commands.CommandTestUtil.VALID_ADDRESS_AMY;
import static seedu.academyassist.logic.commands.CommandTestUtil.VALID_EMAIL_AMY;
import static seedu.academyassist.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.academyassist.logic.commands.CommandTestUtil.VALID_PHONE_AMY;
import static seedu.academyassist.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.academyassist.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.academyassist.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.academyassist.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.academyassist.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.academyassist.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.academyassist.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.academyassist.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.academyassist.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.academyassist.logic.Messages;
import seedu.academyassist.logic.commands.EditCommand;
import seedu.academyassist.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.academyassist.model.person.Address;
import seedu.academyassist.model.person.Email;
import seedu.academyassist.model.person.Ic;
import seedu.academyassist.model.person.Name;
import seedu.academyassist.model.person.Phone;
import seedu.academyassist.model.tag.Tag;
import seedu.academyassist.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {

    private static final String TAG_EMPTY = " " + PREFIX_TAG;

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no id specified
        assertParseFailure(parser, VALID_NAME_AMY, MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, "F1264567X", EditCommand.MESSAGE_NOT_EDITED);

        // no id and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        // length exceeds 9
        assertParseFailure(parser, "F12645678X" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // incorrect NRIC number format
        assertParseFailure(parser, "0" + NAME_DESC_AMY, MESSAGE_INVALID_FORMAT);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "F1264567X some random string", MESSAGE_INVALID_FORMAT);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "F1264567X j/ string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, "F1264567X" + INVALID_NAME_DESC, Name.MESSAGE_CONSTRAINTS); // invalid name
        assertParseFailure(parser, "F1264567X" + INVALID_PHONE_DESC, Phone.MESSAGE_CONSTRAINTS); // invalid phone
        assertParseFailure(parser, "F1264567X" + INVALID_EMAIL_DESC, Email.MESSAGE_CONSTRAINTS); // invalid email
        assertParseFailure(parser, "F1264567X" + INVALID_ADDRESS_DESC, Address.MESSAGE_CONSTRAINTS); // invalid address
        assertParseFailure(parser, "F1264567X" + INVALID_TAG_DESC, Tag.MESSAGE_CONSTRAINTS); // invalid tag

        // invalid phone followed by valid email
        assertParseFailure(parser, "F1264567X" + INVALID_PHONE_DESC + EMAIL_DESC_AMY, Phone.MESSAGE_CONSTRAINTS);

        // while parsing {@code PREFIX_TAG} alone will reset the tags of the {@code Person} being edited,
        // parsing it together with a valid tag results in error
        assertParseFailure(parser, "F1264567X" + TAG_DESC_FRIEND + TAG_DESC_HUSBAND + TAG_EMPTY,
                Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "F1264567X" + TAG_DESC_FRIEND + TAG_EMPTY + TAG_DESC_HUSBAND,
                Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "F1264567X" + TAG_EMPTY + TAG_DESC_FRIEND + TAG_DESC_HUSBAND,
                Tag.MESSAGE_CONSTRAINTS);

        // multiple invalid values, but only the first invalid value is captured
        assertParseFailure(parser, "F1264567X" + INVALID_NAME_DESC + INVALID_EMAIL_DESC + VALID_ADDRESS_AMY
                        + VALID_PHONE_AMY, Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        Ic targetNric = new Ic("F1264567X");
        String userInput = targetNric + PHONE_DESC_BOB + TAG_DESC_HUSBAND
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY + NAME_DESC_AMY + TAG_DESC_FRIEND;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_AMY).withAddress(VALID_ADDRESS_AMY)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
        EditCommand expectedCommand = new EditCommand(targetNric, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_someFieldsSpecified_success() {
        Ic targetNric = new Ic("F1264567X");
        String userInput = targetNric + PHONE_DESC_BOB + EMAIL_DESC_AMY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_BOB)
                .withEmail(VALID_EMAIL_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetNric, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        // name
        Ic targetNric = new Ic("F1264567X");
        String userInput = targetNric + NAME_DESC_AMY;
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY).build();
        EditCommand expectedCommand = new EditCommand(targetNric, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // phone
        userInput = targetNric + PHONE_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withPhone(VALID_PHONE_AMY).build();
        expectedCommand = new EditCommand(targetNric, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // email
        userInput = targetNric + EMAIL_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withEmail(VALID_EMAIL_AMY).build();
        expectedCommand = new EditCommand(targetNric, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // address
        userInput = targetNric + ADDRESS_DESC_AMY;
        descriptor = new EditPersonDescriptorBuilder().withAddress(VALID_ADDRESS_AMY).build();
        expectedCommand = new EditCommand(targetNric, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);

        // tags
        userInput = targetNric + TAG_DESC_FRIEND;
        descriptor = new EditPersonDescriptorBuilder().withTags(VALID_TAG_FRIEND).build();
        expectedCommand = new EditCommand(targetNric, descriptor);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleRepeatedFields_failure() {
        // More extensive testing of duplicate parameter detections is done in
        // AddCommandParserTest#parse_repeatedNonTagValue_failure()

        // valid followed by invalid
        String targetId = "F1264567X";
        String userInput = targetId + INVALID_PHONE_DESC + PHONE_DESC_BOB;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid followed by valid
        userInput = targetId + PHONE_DESC_BOB + INVALID_PHONE_DESC;

        assertParseFailure(parser, userInput, Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // mulltiple valid fields repeated
        userInput = targetId + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY
                + TAG_DESC_FRIEND + PHONE_DESC_AMY + ADDRESS_DESC_AMY + EMAIL_DESC_AMY + TAG_DESC_FRIEND
                + PHONE_DESC_BOB + ADDRESS_DESC_BOB + EMAIL_DESC_BOB + TAG_DESC_HUSBAND;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));

        // multiple invalid values
        userInput = targetId + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC
                + INVALID_PHONE_DESC + INVALID_ADDRESS_DESC + INVALID_EMAIL_DESC;

        assertParseFailure(parser, userInput,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS));
    }

    @Test
    public void parse_resetTags_success() {
        Ic targetNric = new Ic("F1264567X");
        String userInput = targetNric + TAG_EMPTY;

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withTags().build();
        EditCommand expectedCommand = new EditCommand(targetNric, descriptor);

        assertParseSuccess(parser, userInput, expectedCommand);
    }
}
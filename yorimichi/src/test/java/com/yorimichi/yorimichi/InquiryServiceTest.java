package com.yorimichi.yorimichi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.yorimichi.yorimichi.domain.inquiry.dto.InquiryCreateRequestDto;
import com.yorimichi.yorimichi.domain.inquiry.entity.Inquiry;
import com.yorimichi.yorimichi.domain.inquiry.repository.InquiryMapper;
import com.yorimichi.yorimichi.domain.inquiry.service.InquiryService;
import com.yorimichi.yorimichi.domain.user.repository.UserMapper;
import com.yorimichi.yorimichi.global.error.CustomException;
import com.yorimichi.yorimichi.global.error.ErrorCode;

class InquiryServiceTest {

    private final InquiryMapper inquiryMapper = mock(InquiryMapper.class);
    private final InquiryService service = new InquiryService(inquiryMapper, mock(UserMapper.class));

    private InquiryCreateRequestDto request() {
        InquiryCreateRequestDto request = new InquiryCreateRequestDto();
        request.setCategory("DELIVERY");
        request.setTitle("  配送について  ");
        request.setContent("  いつ届きますか  ");
        return request;
    }

    @Test
    void ownerCanEditWaitingInquiry() {
        when(inquiryMapper.updatePending(7L, 9L, "DELIVERY", "配送について", "いつ届きますか"))
                .thenReturn(1);

        service.updatePending(9L, 7L, request());

        verify(inquiryMapper).updatePending(7L, 9L, "DELIVERY", "配送について", "いつ届きますか");
    }

    @Test
    void anotherMembersInquiryCannotBeEdited() {
        when(inquiryMapper.findById(7L)).thenReturn(Inquiry.builder().inquiryId(7L).memberId(8L).status("WAITING").build());

        CustomException error = assertThrows(CustomException.class,
                () -> service.updatePending(9L, 7L, request()));

        assertEquals(ErrorCode.INQUIRY_NOT_OWNER, error.getErrorCode());
    }

    @Test
    void inquiryCannotBeEditedAfterAnswerArrives() {
        when(inquiryMapper.findById(7L)).thenReturn(Inquiry.builder().inquiryId(7L).memberId(9L).status("ANSWERED").build());

        CustomException error = assertThrows(CustomException.class,
                () -> service.updatePending(9L, 7L, request()));

        assertEquals(ErrorCode.INQUIRY_ALREADY_ANSWERED, error.getErrorCode());
    }
}

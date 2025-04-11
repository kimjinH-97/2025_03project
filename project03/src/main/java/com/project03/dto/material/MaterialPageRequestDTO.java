package com.project03.dto.material;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialPageRequestDTO {
    @Builder.Default
    private int page = 1;
    @Builder.Default
    private int size = 10;

    private String type;

    private String keyword;

    public String[] getTypes(){
        if (type == null || type.isEmpty()){
            return null;
        }
        return type.split("");
    }

    public Pageable getPageable(String...props){
        return PageRequest.of(this.page -1, this.size, Sort.by(props).descending());
    }

    private String link;

    public String getLink() {
        if(link == null) {
            StringBuilder builder = new StringBuilder();
            // page와 page size에 대한 링크 처리
            builder.append("page=" + this.page);
            builder.append("&size=" + this.size);

            if(type != null && type.length() > 0) {
                builder.append("&type=" + type);
            }
            if(keyword != null) {
                try {
                    builder.append("&keyword=" + URLEncoder.encode(keyword,"UTF-8"));  // 한글처리를 위한 코드
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            link = builder.toString();
        }

        return link;
    }
}

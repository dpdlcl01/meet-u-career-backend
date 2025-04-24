package com.highfive.meetu.domain.community.personal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsArticle {
  private String title;
  private String description;
  private String url;
  private String publishedAt;
  private NewsSource source;
}


package express.japanese.botto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import javax.annotation.Nonnull;
import java.awt.Color;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RichEmbed {
    private List<MessageEmbed.Field> fields;
    private Color color = Color.WHITE;
    private String description = null;
    private String title = null;
    private String avatarRedirect = null;
    private String avatar = null;
    private String footerText = null;
    private String footerIcon = null;
    private TemporalAccessor time = null;
    private String image = null;
    private String thumbnail = null;
    public RichEmbed() {
        fields = new ArrayList<>();
    }

    public RichEmbed setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    public RichEmbed setTime(TemporalAccessor time) {
        this.time = time;
        return this;
    }

    public RichEmbed setColor(Color color) {
        this.color = color;
        return this;
    }

    public RichEmbed setDescription(String description) {
        this.description = description;
        return this;
    }

    public RichEmbed setTitle(String authorName) {
        this.title = authorName;
        return this;
    }

    public RichEmbed setImage(String image) {
        this.image = image;
        return this;
    }

    public RichEmbed setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public RichEmbed setFields(MessageEmbed.Field[] fields) {
        this.fields.addAll(Arrays.asList(fields));
        return this;
    }
    public RichEmbed setFields(List<MessageEmbed.Field> fields) {
        this.fields = fields;
        return this;
    }

    public RichEmbed setAvatarRedirect(String avatarRedirect) {
        this.avatarRedirect = avatarRedirect;
        return this;
    }

    public RichEmbed setFooterAsCurrentTime() {
        this.footerText = String.valueOf(new Date());
        return this;
    }
    public static String getCurrentTime() {
        return String.valueOf(new Date());
    }

    public RichEmbed setFooterText(String footerText) {
        this.footerText = footerText;
        return this;
    }

    public RichEmbed setFooterIcon(String footerIcon) {
        this.footerIcon = footerIcon;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Color getColor() {
        return color;
    }

    public List<MessageEmbed.Field> getFields() {
        return fields;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getAvatarRedirect() {
        return avatarRedirect;
    }

    public String getDescription() {
        return description;
    }

    public String getFooterIcon() {
        return footerIcon;
    }

    public String getFooterText() {
        return footerText;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public TemporalAccessor getTime() {
        return time;
    }

    public String getImage() {
        return image;
    }

    public RichEmbed addField(@Nonnull String name, String desc, boolean inline) {
        if(desc == null || desc.isEmpty())
            desc = "?";
        MessageEmbed.Field field = new MessageEmbed.Field(name, desc, inline);
        this.fields.add(field);
        return this;
    }

    public MessageEmbed build() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(this.title, this.avatarRedirect, this.avatar);
        for(MessageEmbed.Field field : fields) {
            eb.addField(field);
        }
        eb.setThumbnail(this.thumbnail);
        eb.setColor(this.color);
        eb.setDescription(this.description);
        eb.setTimestamp(this.time);
        eb.setFooter(this.footerText, this.footerIcon);
        eb.setImage(this.image);
        return eb.build();
    }

    @Override
    public String toString() {
        return "RichEmbed{" +
                "fields=" + fields +
                ", color=" + color +
                ", description='" + description + '\'' +
                ", title='" + title + '\'' +
                ", avatarRedirect='" + avatarRedirect + '\'' +
                ", avatar='" + avatar + '\'' +
                ", footerText='" + footerText + '\'' +
                ", footerIcon='" + footerIcon + '\'' +
                ", time=" + time +
                ", image='" + image + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}

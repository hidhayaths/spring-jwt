package app.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;


/**
 * @author Hidhayath
 * @version 1.0
 * @since 06/08/2022
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="roles")
public class AppRole {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @Column(nullable = false,unique = true)
    private String name;

    private String description;
}

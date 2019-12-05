package skyjacker.entities;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "fitment_notes")
public class FitmentNote {

    @Id
    @Column(name = "NOTE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fitNoteID;

    @Column(name = "NOTE_NAME")
    private String fitNote;

    @ManyToMany(mappedBy = "fitNotes")
    private Set<Fitment> fitments = new HashSet<>();

    public String getFitNote() {
        return fitNote;
    }
    public void setFitNote(String fitNote) {
        this.fitNote = fitNote;
    }
    public Integer getFitNoteID() {
        return fitNoteID;
    }
    public void setFitNoteID(Integer fitNoteID) {
        this.fitNoteID = fitNoteID;
    }
    public Set<Fitment> getFitments() {
        return fitments;
    }
    public void setFitments(Set<Fitment> fitments) {
        this.fitments = fitments;
    }

    @Override
    public String toString() {
        return "FitmentNote{" +
                "fitNoteID=" + fitNoteID +
                ", fitNote='" + fitNote + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FitmentNote)) return false;
        FitmentNote that = (FitmentNote) o;
        return getFitNote().equals(that.getFitNote());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFitNote());
    }
}

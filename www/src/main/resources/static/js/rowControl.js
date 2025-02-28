/*<![CDATA[*/
document.getElementById('insert-menu').addEventListener('click', () => {
    const table = document.getElementById("table-form");
    const index = table.rows.length - 1;
    const newRow = table.insertRow(index);
    const cellLength = table.rows[0].cells.length;

    for(let i = 0; i < cellLength; i++){
        const newCell = newRow.insertCell(i);
        let temp = ``;
        if(i == 0){
            temp = `<td class='width-16 text-center'><input type='text' class="inner-width form-control" name='item'/></td>`;
        }
        else if(i == 1){
            temp = `<td class='width-16 text-center'><input type='text' class="inner-width form-control" name='type'/></td>`;
        }
        else if(i == 2){
            temp = `<td class='width-16 text-center'><input type='text' class="inner-width form-control" name='grade'/></td>`;
        }
        else if(i == 3){
            temp = `<td class='width-16 text-center'><input type='text' class="inner-width form-control" name='period'/></td>`;
        }
        else if(i == 4){
            temp = `<td class='width-16 text-center'><input type='text' class="inner-width form-control" name='quantity'/></td>`;
        }
        else {
            temp = `<td class='width-16 text-center'><button type="button" class="inner-width form-control delete-menu" onclick="deleteMenuEvent(this)">행 삭제</button></td>`;
        }
        newCell.insertAdjacentHTML('afterbegin', temp);
});

function deleteMenuEvent(btn) {
    var tr = btn.closest("tr");
    tr.remove();
}
/*]]>*/
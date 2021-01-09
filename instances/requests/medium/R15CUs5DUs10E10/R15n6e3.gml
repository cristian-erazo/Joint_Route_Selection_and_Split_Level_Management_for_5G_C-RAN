graph [
  node [
    id 0
    label "0"
    type 2
    prc 5
  ]
  node [
    id 1
    label "1"
    type 2
    prc 3
  ]
  node [
    id 2
    label "2"
    type 2
    prc 3
  ]
  node [
    id 3
    label "3"
    type 1
    prc 2
    ant 6
    prb 5
    x 37
    y 110
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 8
    prb 5
    x 69
    y 102
  ]
  node [
    id 5
    label "5"
    type 1
    prc 4
    ant 5
    prb 6
    x 78
    y 110
  ]
  edge [
    source 0
    target 3
    bandwith 622
    delay 222
  ]
  edge [
    source 1
    target 5
    bandwith 257
    delay 181
  ]
  edge [
    source 2
    target 4
    bandwith 362
    delay 456
  ]
]

graph [
  node [
    id 0
    label "0"
    type 2
    prc 2
  ]
  node [
    id 1
    label "1"
    type 2
    prc 5
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
    type 2
    prc 5
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 4
    prb 5
    x 49
    y 106
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 4
    prb 4
    x 65
    y 116
  ]
  node [
    id 6
    label "6"
    type 1
    prc 1
    ant 3
    prb 6
    x 57
    y 20
  ]
  node [
    id 7
    label "7"
    type 1
    prc 2
    ant 6
    prb 5
    x 87
    y 37
  ]
  edge [
    source 0
    target 5
    bandwith 297
    delay 307
  ]
  edge [
    source 1
    target 4
    bandwith 946
    delay 138
  ]
  edge [
    source 2
    target 7
    bandwith 109
    delay 342
  ]
  edge [
    source 3
    target 6
    bandwith 668
    delay 431
  ]
]
